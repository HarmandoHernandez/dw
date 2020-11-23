package com.utng.discoverw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_details_post.*

class DetailsPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_post)

        // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val topic = intent.getSerializableExtra("post") as Post

        txtPrueba.text = topic.title

        seeMap.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java).apply {
                putExtra("title", topic.title)
                // TODO : Sustituir por cordenadas del Post
                putExtra("lat", 21.15794952216306)
                putExtra("long", -100.93435016893258)
            }
            startActivity(mapIntent)
        }
    }
}