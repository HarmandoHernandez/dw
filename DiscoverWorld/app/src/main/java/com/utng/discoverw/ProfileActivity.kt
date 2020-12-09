package com.utng.discoverw

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private val ddBb = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val aAuth = FirebaseAuth.getInstance()

    //onBackPressed() Retornar a la pantalla anterior
    override fun onCreate(savedInstanceState: Bundle?) {
        ddBb.firestoreSettings = settings

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        /** Initial data */
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", "")
        val name = prefs.getString("displayName", "")
        val photo = prefs.getString("photoUrl", "")
        if (email == "") {
            openAuth()
        } else {
            setup(name ?: "", photo ?: "")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStart() {
        super.onStart()
        menu()
    }
    /**
     * Load functions
     */
    private fun setup(name: String, photo: String) {
        profile_name.text = name
        Picasso.with(this)
                .load(photo)
                .into(profile_image)

        btnAddPost.setOnClickListener {
            startActivity(Intent(this, PhotoActivity::class.java))
        }

        bottomAppBar.setOnMenuItemClickListener {
            println(it.itemId)
            when (it.itemId) {
                /*R.id.home -> {
                    //startActivity(Intent(this, HomeActivity::class.java))
                    true
                }*/
                R.id.postSaves -> {
                    startActivity(Intent(this, PostSavesActivity::class.java))
                    true
                }
                R.id.close -> {
                    closeSession()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Close session
     */
    private fun closeSession() {
        val confirm = AlertDialog.Builder(this)
        confirm.setTitle("Session")
        confirm.setMessage("¿Seguro que quieres cerrar session?")
        confirm.setCancelable(false)
        confirm.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialogo1, id ->
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        })
        confirm.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogo1, id -> /* Do something */ })
        confirm.show()
    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }


    /**
     * Obtiene las keys de los post que guardo el usuario
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun menu() {
        val postKeys = arrayListOf<String>()
        val docRef = ddBb.collection("users")
                .document(aAuth.currentUser?.uid.toString())
                .collection("posts")
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.documents.size > 0) {
                        for (saved in document.documents){
                            postKeys.add(saved.data?.get("key").toString())
                        }
                        getPosts(postKeys)
                    } else {
                        Log.d(this.localClassName, "No such Posts")
                        Toast.makeText(this, "No se encontraron Post", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(this.localClassName, "get Posts failed with ", exception)
                    Toast.makeText(this, "No se ha podido obtener ningun dato", Toast.LENGTH_SHORT).show()
                }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getPosts(listKeys: ArrayList<String>) {
        val postItems = arrayListOf<Post>()
        for (key in listKeys) {
            val docRef = ddBb.collection("posts").document(key)
            docRef.get().addOnCompleteListener {
                postItems.add(Post(
                        key,
                        it.result?.data?.get("title").toString(),
                        key,
                        it.result?.data?.get("description").toString(),
                        it.result?.data?.get("lat").toString(),
                        it.result?.data?.get("long").toString()
                ))
                if (postItems.size == listKeys.size) {
                    getUrlPosts(postItems)
                }
            }.addOnFailureListener {
                Log.d(this.localClassName, "get Posts failed with ", it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
                    Log.w("ERROR","ERROR AL OBTENER IMAGEN")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun toPostAdapter(listPost: ArrayList<Post>) {
        list.isNestedScrollingEnabled = true;
        list.adapter = PostSavesAdapter(this, listPost)

        list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, DetailsPostActivity::class.java)
            intent.putExtra("post", listPost[position])
            startActivity(intent)
        }
    }
}