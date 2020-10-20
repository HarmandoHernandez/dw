package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Firebase Authentication using a Google ID Token.
 */
class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash
        Thread.sleep(500) // HACK
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        //authLayout.visibility = View.VISIBLE
    }

    private fun setup() {
        val btnLogIn = findViewById<Button>(R.id.btnLogin)
        btnLogIn.setOnClickListener {
            // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            // Build a GoogleSignInClient with the options specified by gso.
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut() // Delete a last account
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        // [START_EXCLUDE silent]
        //showProgressBar()
        // [END_EXCLUDE]
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if(account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                    // Signed in successfully, show authenticated UI.
                    if(it.isSuccessful) {
                        Log.d(this.localClassName, "signInWithCredential:success")
                        showHome(account.email ?: "", account.displayName ?: "", account.photoUrl.toString() ?: "")
                    } else {
                        Log.d(this.localClassName, "signInWithCredential:failure")
                        showAlert()
                    }
                }
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(this.localClassName, "signInResult:fail cause=" + e.message)
            showAlert()
        }
    }

    private fun showHome(email: String, displayName: String, photoUrl: String){
        // [START_EXCLUDE]
        //hideProgressBar()
        // [END_EXCLUDE]
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("displayName", displayName)
            putExtra("photoUrl", photoUrl)
        }
        startActivity(homeIntent)
    }

    private fun showAlert() {
        // [START_EXCLUDE]
        //hideProgressBar()
        // [END_EXCLUDE]
        Toast.makeText(applicationContext, getString(R.string.app_alert), Toast.LENGTH_LONG).show()
    }

    private fun session(){
        // Session opening
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val displayName = prefs.getString("displayName", null)
        val photoUrl = prefs.getString("photoUrl", "")

        if(email != null && displayName != null){
            // authLayout.visibility = View.INVISIBLE
            showHome(email, displayName, photoUrl ?: "")
        }
    }
    // TODO : Loading screen (Al seleccionar cuenta de Gmail)
}