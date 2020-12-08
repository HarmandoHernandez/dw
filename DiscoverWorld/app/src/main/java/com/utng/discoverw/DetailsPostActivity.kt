package com.utng.discoverw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details_post.*

class DetailsPostActivity : AppCompatActivity() {

    private val ddBb = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private val aAuth = FirebaseAuth.getInstance()
    private lateinit var uid: String
    private lateinit var topic: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        ddBb.firestoreSettings = settings
        uid = aAuth.currentUser?.uid.toString()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_post)

        // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        topic = intent.getSerializableExtra("post") as Post

        textPostTitle.text = topic.title
        textPostDescription.text = topic.description
        Picasso.with(this)
                .load(topic.image)
                .error(R.drawable.ic_error)
                .into(postView)

        seeMap.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java).apply {
                putExtra("title", topic.title)
                // TODO : Sustituir por cordenadas del Post
                putExtra("lat", 21.15794952216306)
                putExtra("long", -100.93435016893258)
            }
            startActivity(mapIntent)
        }

        savePost.setOnClickListener {
            val map = mutableMapOf<String, Any>()
            map["key"] = topic.key

            ddBb.collection("users")
                    .document(uid)
                    .collection("saves")
                    .add(map)
                    .addOnSuccessListener {
                        Log.i("INFO","Post saved")
                    }
                    .addOnFailureListener { e ->
                        Log.w("ERROR","Post save error $e")
                    }
            savePost.setImageResource(R.drawable.ic_bookmark)
        }

    }
}