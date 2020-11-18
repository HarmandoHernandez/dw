package com.utng.discoverw

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*

/**
 * Firebase Authentication using a Google ID Token.
 */
@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        /** Splash */
        Thread.sleep(500) // HACK
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setup()
    }

    /**
     * Load functions
     */
    private fun setup() {
        session()
        btnLogin.setOnClickListener {
            getAccount()
        }
    }

    /**
     * Get accounts from phone
     */
    private fun getAccount(){
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

    /**
     * Response of user (select a email)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /**
     * Valid if account was successful
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        val loading = ProgressDialog(this@AuthActivity)
        loading.setTitle("Acceso con Gmail")
        loading.setMessage("Cargando, por favor espere...")
        loading.show()

        try {
            val account = completedTask.getResult(ApiException::class.java)
            if(account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                    if(it.isSuccessful) {
                        loading.dismiss()
                        showHome(account.email ?: "", account.displayName ?: "", account.photoUrl.toString() ?: "")
                    } else {
                        loading.dismiss()
                        showAlert()
                    }
                }
            } else {
                loading.dismiss()
                showAlert()
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            loading.dismiss()
            showAlert()
        }
    }

    /**
     * Access to Home screen
     */
    private fun showHome(email: String, displayName: String, photoUrl: String){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("displayName", displayName)
            putExtra("photoUrl", photoUrl)
        }
        startActivity(homeIntent)
        finish()
    }

    /**
     * Alert of error
     */
    private fun showAlert() {
        Toast.makeText(applicationContext, getString(R.string.app_alert), Toast.LENGTH_LONG).show()
    }

    /**
     * Valid if exist a session opening
     */
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val displayName = prefs.getString("displayName", null)
        val photoUrl = prefs.getString("photoUrl", "")

        if(email != null && displayName != null){
            showHome(email, displayName, photoUrl ?: "")
        }
    }
}