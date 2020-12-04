package com.utng.discoverw

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post_saves.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


class ProfileActivity : AppCompatActivity() {
    //onBackPressed() Retornar a la pantalla anterior
    override fun onCreate(savedInstanceState: Bundle?) {
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

    /**
     * Load functions
     */
    private fun setup(name: String, photo: String) {
        // menu()
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
                R.id.home -> {
                    //startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
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

    private fun menu() {
        val listPostSaves = ArrayList<Post>()
        listPostSaves.add(Post("Celebration",
                "https://digitalsevilla.com/wp-content/uploads/2019/03/celebraci%C3%B3n-de-eventos.jpg",
                "Celebrate who you are in your deepest heart. Love your self and the world will love you.",
                "21.15794952216306",
                "-100.93435016893258"))
        listPostSaves.add(Post("Party",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQK4WEZ21bnJ1v60mpxn47IBJRtTmG0lQ4edQ&usqp=CAU",
                "You gotta have life your way.",
                "21.15794952216306",
                "-100.93435016893258"))
        listPostSaves.add(Post("Exercise",
                "https://static01.nyt.com/images/2020/03/10/well/physed-immune1/physed-immune1-mobileMasterAt3x.jpg",
                "Whenever I feel the need to exercise, I like down until it goes away.",
                "21.15794952216306",
                "-100.93435016893258"))
        listPostSaves.add(Post("Nature",
                "https://assets.unenvironment.org/styles/article_billboard_image/s3/2020-05/nature-3294681_1920%20%281%29.jpg?null&amp;h=ebad6883&amp;itok=iV1MUd_a",
                "In every walk in with nature on receives for more tha he seeks.",
                "21.15794952216306",
                "-100.93435016893258"))

        val adapter = PostSavesAdapter(this, listPostSaves)
        list.adapter = adapter // LiatView

        list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, DetailsPostActivity::class.java)
            intent.putExtra("post", listPostSaves[position])
            startActivity(intent)
        }

    }

    /**
     * Throw screen Auth if there isn't email
     */
    private fun openAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}