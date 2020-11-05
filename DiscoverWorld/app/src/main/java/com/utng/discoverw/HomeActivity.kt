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
        if ( email == "") {
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

        validAccount(idX ?: "", email ?: "",displayName ?: "",photoUrl ?: "")

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
                        if(document.data?.get("email") == email) {
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
            registerAccount(idX ?: "",email ?: "",name ?: "",photo ?: "")
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
                    println( "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    println("Error adding document $e")
                }
    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth(){
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun actionbutton() {
        fab1.setOnClickListener {
            var iModo = true
            fab1.setOnClickListener {
                val sMsg: String
                if (iModo){
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
        postItemCelebration.postURL = "http://www.infinityandroid.com/videos/video1.mp4"
        postItemCelebration.postTitle = "Celebration"
        postItemCelebration.postDescription = "Celebrate who you are in your deepest heart. Love your self and the world will love you."
        postItems.add(postItemCelebration)

        val postItemParty = PostItem()
        postItemParty.postURL = "http://www.infinityandroid.com/videos/video2.mp4"
        postItemParty.postTitle = "Party"
        postItemParty.postDescription = "You gotta have life your way."
        postItems.add(postItemParty)

        val postItemExercise = PostItem()
        postItemExercise.postURL = "http://www.infinityandroid.com/videos/video3.mp4"
        postItemExercise.postTitle = "Exercise"
        postItemExercise.postDescription = "Whenever I feel the need to exercise, I like down until it goes away."
        postItems.add(postItemExercise)

        val postItemNature = PostItem()
        postItemNature.postURL = "http://www.infinityandroid.com/videos/video4.mp4"
        postItemNature.postTitle = "Nature"
        postItemNature.postDescription = "In every walk in with nature on receives for more tha he seeks."
        postItems.add(postItemNature)

        val postItemTravel = PostItem()
        postItemTravel.postURL = "http://www.infinityandroid.com/videos/video5.mp4"
        postItemTravel.postTitle = "travel"
        postItemTravel.postDescription = "It's better to travel well than to arrive"
        postItems.add(postItemTravel)

        val postItemChill = PostItem()
        postItemChill.postURL = "http://www.infinityandroid.com/videos/video6.mp4"
        postItemChill.postTitle = "Chill"
        postItemChill.postDescription = "Life is so much easier when you just chill out."
        postItems.add(postItemChill)

        val postItemLove = PostItem()
        postItemLove.postURL = "http://www.infinityandroid.com/videos/video7.mp4"
        postItemLove.postTitle = "Love"
        postItemLove.postDescription = "The best thing to hold onto on life is each other"
        postItems.add(postItemLove)

        postViewPager.adapter = PostAdapter(postItems)
    }
}