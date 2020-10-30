package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    //onBackPressed() Retornar a la pantalla anterior
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // TODO : Youtube "Modern Dashboard UI Design Android Studio Tutorial"

        /** Initial data */
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", "")
        val name = prefs.getString("displayName", "")
        val photo = prefs.getString("photoUrl", "")
        if ( email == "") {
            openAuth()
        } else {
            setup(name ?: "", photo ?: "")
        }
    }

    /**
     * Load functions
     */
    private fun setup(name: String, photo: String) {
        profile_name.text = name
        imageView3.setImageURI(Uri.parse( photo )) //profile_image

        btnLogOut.setOnClickListener {
            closeSession()
        }
        btnAddPost.setOnClickListener {
            startActivity(Intent(this, AddPActivity::class.java))
        }
        btnSavePost.setOnClickListener {
            startActivity(Intent(this, SavesPActivity::class.java))
        }
    }

    /**
     * Close session
     */
    private fun closeSession(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth(){
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}