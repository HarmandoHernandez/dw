package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_home.*

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_save_button.*

class HomeActivity : AppCompatActivity() {

    private val ddBb = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()

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
        actionbutton()
        account()
        posts()
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
    }

    /**
     * Save user's data
     */
    private fun account() {
        val aAuth = FirebaseAuth.getInstance()
        val idX = aAuth.currentUser?.uid
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val displayName = bundle?.getString("displayName")
        val photoUrl = bundle?.getString("photoUrl")

        validAccount(idX ?: "", email ?: "", displayName ?: "", photoUrl ?: "")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("displayName", displayName)
        prefs.putString("photoUrl", photoUrl)
        prefs.apply()
    }

    /**
     * Valid if exist an account and decide if need create or update account
     */
    private fun validAccount(idX: String, email: String, name: String, photo: String) {
        var exist = false
        val docRef = ddBb.collection("users").document(idX)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.data?.get("email") == email) {
                            exist = true
                            updateAccount(idX, name, document.data?.get("displayName").toString(), photo, document.data?.get("photoUrl").toString())
                        }
                    } else {
                        Log.d(this.localClassName, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(this.localClassName, "get failed with ", exception)
                }

        if (!exist) {
            registerAccount(idX ?: "", email ?: "", name ?: "", photo ?: "")
        }
    }

    private fun updateAccount(idX: String, nameNew: String, nameOld: String, photoNew: String, photoOld: String) {
        val docRef = ddBb.collection("users").document(idX)
        if (nameNew != nameOld) {
            docRef
                    .update("displayName", photoNew)
                    .addOnSuccessListener { Log.d("TAGX", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
        }
        if (photoNew != photoOld) {
            docRef
                    .update("photoUrl", photoNew)
                    .addOnSuccessListener { Log.d("TAGX", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("TAGX", "Error updating document", e) }
        }
    }

    private fun registerAccount(idX: String, email: String, name: String, photo: String) {
        val map = mutableMapOf<String, Any>()
        map["email"] = email
        map["displayName"] = name
        map["photoUrl"] = photo

        ddBb.collection("users").document(idX).set(map)
                .addOnSuccessListener {
                    println("DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    println("Error adding document $e")
                }
    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun actionbutton() {
        fab1.setOnClickListener {
            var iModo = true
            fab1.setOnClickListener {
                val sMsg: String
                if (iModo) {
                    sMsg = "Guardado con exito"
                    val bt = findViewById<View>(R.id.fab1) as FloatingActionButton
                    bt.setImageResource(R.drawable.estrella)
                    bt.backgroundTintList = ColorStateList.valueOf(Color.BLUE)
                } else {
                    sMsg = "Cancelado"
                    val bt = findViewById<View>(R.id.fab1) as FloatingActionButton
                    bt.setImageResource(R.drawable.reloj)
                    bt.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF52658F"))
                }
                iModo = !iModo
                supportFragmentManager
                        .beginTransaction()
                        .commit()

                Snackbar.make(fab1, sMsg, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
            }
        }
    }

    /**
     * Example Post with hard code
     */
    private fun posts() {
        val postViewPager = findViewById<ViewPager2>(R.id.postViewPager)
        val postItems = arrayListOf<PostItem>()
        val postItemCelebration = PostItem()

        postItemCelebration.postURL = "https://i.pinimg.com/originals/5e/32/50/5e3250446fc22f7890a9ed178758efc2.jpg"
        postItemCelebration.postTitle = "Celebration"
        postItemCelebration.postDescription = "Celebrate who you are in your deepest heart. Love your self and the world will love you."
        postItems.add(postItemCelebration)

        val postItemParty = PostItem()
        postItemParty.postURL = "https://lh3.googleusercontent.coms";//"https://lh3.googleusercontent.com/proxy/vqPuCYww6uOJUi9iR7_ERy1C2boSmwlT119IQX_qc8878TElaRn4wXMOdi2BXBMWDFbchykF0T220WoUr_n9fz-6xdQ0kT2m7hoZNFbFudHDkpPOdv4x2tKmX4kwE8BrbotHltuCPOdimaMOlj1tmjChSNqCpoU6w6egFVbDNA";
        postItemParty.postTitle = "Party"
        postItemParty.postDescription = "You gotta have life your way."
        postItems.add(postItemParty)

        val postItemExercise = PostItem()
        postItemExercise.postURL = "https://bloximages.chicago2.vip.townnews.com/greenevillesun.com/content/tncms/assets/v3/editorial/1/78/178c57a5-acfe-535e-ac2e-de2db173ace0/5f43c2f6edeeb.image.jpg?resize=400%2C600"
        postItemExercise.postTitle = "Exercise"
        postItemExercise.postDescription = "Whenever I feel the need to exercise, I like down until it goes away."
        postItems.add(postItemExercise)

        val postItemNature = PostItem()
        postItemNature.postURL = "https://i.pinimg.com/originals/e0/3f/0a/e03f0aa3ff39bcd17c40b488ba732067.jpg"
        postItemNature.postTitle = "Nature"
        postItemNature.postDescription = "In every walk in with nature on receives for more tha he seeks."
        postItems.add(postItemNature)

        postViewPager.adapter = PostAdapter(postItems)
    }
}