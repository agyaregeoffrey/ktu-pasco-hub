package com.dev.gka.ktupascohub

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.activities.PastQuestionsActivity
import com.dev.gka.ktupascohub.databinding.ActivityMainBinding
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Constants
import com.dev.gka.ktupascohub.utilities.Constants.RC_SIGN_IN
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Timber.e("Sign in failed: ${e.message}")
                binding.progressHorizontal.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.buttonSignWithGoogle.setOnClickListener {
            binding.progressHorizontal.visibility = View.VISIBLE
            val signInIntent = googleSignInClient.signInIntent
            startForResult.launch(signInIntent)
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.progressHorizontal.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val student = user?.let {
                        Student(it.displayName, it.email, it.phoneNumber, it.photoUrl)
                    }
                    if (student != null) {
                        PrefManager.getInstance(applicationContext).saveUserInfo(student)
                    }
                    startActivity(Intent(this, PastQuestionsActivity::class.java))
                    finish()
                }
            }.addOnFailureListener {
                Timber.e("Sign in failed: ${it.message}")
                binding.progressHorizontal.visibility = View.GONE
            }
    }

}