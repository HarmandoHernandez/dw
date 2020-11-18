package com.utng.discoverw

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
    }
}