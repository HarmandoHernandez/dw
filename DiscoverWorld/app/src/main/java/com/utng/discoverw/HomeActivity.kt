package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_home.*

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
        val id = aAuth.currentUser?.uid
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val displayName = bundle?.getString("displayName")
        val photoUrl = bundle?.getString("photoUrl")

        if (existAccount(id ?: "")) {
            println("Read account")
        } else {
            registerAccount(id ?: "",email ?: "",displayName ?: "",photoUrl ?: "")
        }
        /**
         * TODO : consultar si ya se tiene un perfil con dicho gmail
         * Op 1-> crear cuenta
         * Op 2-> actualizar datos si han cambiado
         */

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("displayName", displayName)
        prefs.putString("photoUrl", photoUrl)
        prefs.apply()
    }

    private fun existAccount(id: String): Boolean {
        var exist = false
        val docRef = ddBb.collection("Accounts").document(id)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(this.localClassName, "DocumentSnapshot data: ${document.data}")
                        println("XX${document.toString()}")
                        if(document.data == null) {
                            exist = true
                        }
                    } else {
                        Log.d(this.localClassName, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(this.localClassName, "get failed with ", exception)
                }
        return exist
    }

    private fun updateAccount() {

    }

    private fun registerAccount(idX: String, email: String, name: String, photo: String) {
        val map = mutableMapOf<String, Any>()
        map["email"] = email
        map["displayName"] = name
        map["photoUrl"] = photo

        ddBb.collection("users").document(idX).set(map)
                .addOnSuccessListener {
                    //documentReference ->
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