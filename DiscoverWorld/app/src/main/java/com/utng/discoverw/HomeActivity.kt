package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val ddBb = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val aAuth = FirebaseAuth.getInstance()
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        ddBb.firestoreSettings = settings

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setup()
    }

    /**
     * Load functions
     */
    private fun setup() {
        account()
        fab.setOnClickListener {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    /**
     * Valid if exist a session opening
     */
    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", "")
        if (email == "") {
            openAuth()
        }
        posts()
    }

    /**
     * Save user's data
     */
    private fun account() {
        uid = aAuth.currentUser?.uid.toString()
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val displayName = bundle?.getString("displayName")
        val photoUrl = bundle?.getString("photoUrl")

        validAccount(email ?: "", displayName ?: "", photoUrl ?: "")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("displayName", displayName)
        prefs.putString("photoUrl", photoUrl)
        prefs.apply()
    }

    /**
     * Valid if exist an account and decide if need create or update account
     */
    private fun validAccount(email: String, name: String, photo: String) {
        var exist = false
        val docRef = ddBb.collection("users").document(uid)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.data?.get("email") == email) {
                            exist = true
                            updateAccount(name, document.data?.get("displayName").toString(), photo, document.data?.get("photoUrl").toString())
                        }
                    } else {
                        Log.d(this.localClassName, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(this.localClassName, "get failed with ", exception)
                }

        if (!exist) {
            registerAccount(email ?: "", name ?: "", photo ?: "")
        }
    }

    private fun updateAccount(nameNew: String, nameOld: String, photoNew: String, photoOld: String) {
        val docRef = ddBb.collection("users").document(uid)
        if (nameNew != nameOld) {
            docRef
                    .update("displayName", photoNew)
                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
        }
        if (photoNew != photoOld) {
            docRef
                    .update("photoUrl", photoNew)
                    .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
        }
    }

    private fun registerAccount(email: String, name: String, photo: String) {
        val map = mutableMapOf<String, Any>()
        map["email"] = email
        map["displayName"] = name
        map["photoUrl"] = photo

        ddBb.collection("users").document(uid).set(map)
                .addOnSuccessListener {
                    Log.i("TAG", "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.i("TAG", "Error adding document $e")
                }
    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    /**
     * Example Post with hard code
     */
    private fun posts() {
        val postItems = arrayListOf<Post>()
        val docRef = ddBb.collection("posts")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.documents.size > 0) {
                for (post in document.documents) {
                    postItems.add(Post(
                            post.id,
                            post.data?.get("title").toString(),
                            "",
                            post.data?.get("description").toString(),
                            post.data?.get("lat").toString(),
                            post.data?.get("long").toString()))

                    if (postItems.size == document.documents.size) getUrlPosts(postItems)
                }
            } else {
                Log.d(this.localClassName, "No such Posts")
                Toast.makeText(this, "No se encontraron Post", Toast.LENGTH_SHORT).show()
            }
        }
                .addOnFailureListener { exception ->
                    Log.d(this.localClassName, "get Posts failed with ", exception)
                }
    }

    private fun getUrlPosts(listPost: ArrayList<Post>) {
        var i = 0
        for ((index, element) in listPost.withIndex()) {
            storageRef.child(element.key).downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    listPost[index].image = it.result.toString()
                    i++
                    if (i == listPost.size) {
                        toPostAdapter(listPost)
                    }
                } else {
                    Log.w("ERROR", "ERROR AL OBTENER IMAGEN")
                }
            }

        }
    }

    private fun toPostAdapter(listPost: ArrayList<Post>) {
        postViewPager.adapter = PostAdapter(listPost)
        fab1.setOnClickListener {
            val intent = Intent(this, DetailsPostActivity::class.java)
            intent.putExtra("post", listPost[postViewPager.currentItem])
            startActivity(intent)
        }
    }
}