package com.utng.discoverw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post_saves.*
import java.util.*

class PostSavesActivity : AppCompatActivity() {

    private val ddBb = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val aAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        ddBb.firestoreSettings = settings

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_saves)
        menu()
    }

    /**
     * Obtiene las keys de los post que guardo el usuario
     */
    private fun menu() {
        val postKeys = arrayListOf<String>()
        val docRef = ddBb.collection("users")
                .document(aAuth.currentUser?.uid.toString())
                .collection("saves")
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.documents.size > 0) {
                        for (saved in document.documents) {
                            postKeys.add(saved.data?.get("key").toString())
                        }
                        getPosts(postKeys)
                    } else {
                        Log.d(this.localClassName, "No such Posts")
                        Toast.makeText(this, "No se encontraron Post", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(this.localClassName, "get Posts failed with ", exception)
                    Toast.makeText(this, "No se ha podido obtener ningun dato", Toast.LENGTH_SHORT).show()
                }
    }

    private fun getPosts(listKeys: ArrayList<String>) {
        val postItems = arrayListOf<Post>()
        for (key in listKeys) {
            val docRef = ddBb.collection("posts").document(key)
            docRef.get().addOnCompleteListener {
                postItems.add(Post(
                        key,
                        it.result?.data?.get("title").toString(),
                        key,
                        it.result?.data?.get("description").toString(),
                        it.result?.data?.get("lat").toString(),
                        it.result?.data?.get("long").toString()
                ))
                if (postItems.size == listKeys.size) {
                    getUrlPosts(postItems)
                }
            }.addOnFailureListener {
                Log.d(this.localClassName, "get Posts failed with ", it)
            }
        }
    }

    private fun getUrlPosts(listPost: ArrayList<Post>) {
        var i = 0
        for ((index, element) in listPost.withIndex()) {
            storageRef.child(element.key).downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    listPost[index].image = it.result.toString()
                    i++
                    if (i == listPost.size) {
                        toPostAdapter(listPost)
                    }
                } else {
                    Log.w("ERROR", "ERROR AL OBTENER IMAGEN")
                }
            }
        }
    }

    private fun toPostAdapter(listPost: ArrayList<Post>) {
        list.adapter = PostSavesAdapter(this, listPost)

        list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, DetailsPostActivity::class.java)
            intent.putExtra("post", listPost[position])
            startActivity(intent)
        }
    }

}