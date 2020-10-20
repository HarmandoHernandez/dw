package com.utng.discoverw

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        // TODO : Modern Dashboard UI Design Android Studio Tutorial

        val txtProfileName = findViewById<TextView>(R.id.profile_name)
        val txtProfileImage = findViewById<ImageView>(R.id.imageView3)//profile_image)
        val bundle = intent.extras
        txtProfileName.text = bundle?.getString("displayName")
        txtProfileImage.setImageURI(Uri.parse(bundle?.getString("photoUrl")))
    }

}