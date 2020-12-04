package com.utng.discoverw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_post_saves.*
import java.util.*

class PostSavesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_saves)
        menu()
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
        list.adapter = adapter

        list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, DetailsPostActivity::class.java)
            intent.putExtra("post", listPostSaves[position])
            startActivity(intent)
        }

    }
}