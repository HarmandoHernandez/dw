package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_save_button.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        account()
        actionbutton()
        toProfile()
        posts()
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
        /**
         * TODO : consultar si ya se tiene un perfil con dicho gmail
         * Op 1-> crear cuenta
         * Op 2-> actualizar datos si han cambiado
         */
        val bundle = intent.extras
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", bundle?.getString("email"))
        prefs.putString("displayName", bundle?.getString("displayName"))
        prefs.putString("photoUrl", bundle?.getString("photoUrl"))
        prefs.apply()
    }
    private fun actionbutton() {
        fab1.setOnClickListener {
            var iModo = true
            fab1.setOnClickListener {
                var sMsg: String
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
     * Access to Profile screen
     */
    private fun toProfile(){
        fab.setOnClickListener {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    /**
     * Example Post with hard code
     */
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

        postViewPager.adapter = PostAdapter(postItems)
    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth(){
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}