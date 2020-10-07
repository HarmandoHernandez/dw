package com.utng.discoverw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash
        Thread.sleep(1000) // HACK
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
        // title = getText(R.string.app_authentication)
        val btnLogIn = findViewById<Button>(R.id.btnLogin)

        btnLogIn.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut() // Si tenemos 2 cuentas de Google en el dispositivo

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    // Exist session
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if(email != null && provider != null){
            // authLayout.visibility = View.INVISIBLE
            showHome(email, Providertype.valueOf(provider))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(account.email ?: "", Providertype.GOOGLE)
                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }

    private fun showAlert() {
        Toast.makeText(applicationContext, getString(R.string.app_alert), Toast.LENGTH_LONG).show()
    }

    private fun showHome(email: String, provider: Providertype){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

}