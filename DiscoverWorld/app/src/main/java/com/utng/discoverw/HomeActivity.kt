package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

// TODO : No permitir regresar al inicio de session.
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // SETUP
        account()
        setup()
        toProfile()
        posts()
    }

    private fun account() {
        // Save user's data
        val bundle = intent.extras // TODO : delete bundle X2
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", bundle?.getString("email"))
        prefs.putString("displayName", bundle?.getString("displayName"))
        prefs.putString("photoUrl", bundle?.getString("photoUrl"))
        prefs.apply()
    }

    private fun toProfile(){
        val fab: FloatingActionButton = findViewById(R.id.fab)
        val bundle = intent.extras // TODO : delete bundle
        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            val profileIntent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("email", bundle?.getString("email"))
                putExtra("displayName", bundle?.getString("displayName"))
                putExtra("photoUrl", bundle?.getString("photoUrl"))
            }
            startActivity(profileIntent)
        }
    }

    private fun setup() {
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)

        btnLogOut.setOnClickListener {
            // Delete data
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    private fun posts() {
        // POSTS
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

        //postViewPager.setAdapter(PostAdapter(postItems))
        postViewPager.adapter = PostAdapter(postItems)
    }

}