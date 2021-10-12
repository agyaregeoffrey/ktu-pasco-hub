package com.dev.gka.ktupascohub.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityMainBinding
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Constants
import com.dev.gka.ktupascohub.utilities.Constants.FINISH
import com.dev.gka.ktupascohub.utilities.Helpers
import com.dev.gka.ktupascohub.utilities.Helpers.nameFromFirebase
import com.dev.gka.ktupascohub.utilities.Helpers.showSnack
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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

        binding.buttonStudentSignIn.setOnClickListener {
            val email = binding.editTextStudentEmail.text.toString()
            val password = binding.editTextStudentPassword.text.toString()
            if (isFormValidated()) {
                signIn(email, password)
            }
        }

        binding.buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }

    override fun onStart() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, LevelSelectionActivity::class.java))
            finish()
        }
        super.onStart()
    }

    private fun signIn(email: String, password: String) {
        binding.progressHorizontal.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val hasRetriedName = nameFromFirebase(auth, firestore, this)
                val prefs = PrefManager.getInstance(this)
                prefs.accountSelectionOpened(b = true)
                prefs.studentSignInStatus(true)
                Timber.d("$hasRetriedName")
                if (task.isSuccessful) {
                    val broadcast = Intent(FINISH)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast)
                    startActivity(Intent(this, LevelSelectionActivity::class.java))
                    finish()
                } else {
                    showSnack(binding.buttonSignWithGoogle, getString(R.string.sign_failed))
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.progressHorizontal.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val prefs = PrefManager.getInstance(this)
                    if (user != null) {
                        val student = Student(user.displayName, user.email, null, null)
                        prefs.saveUserInfo(student)
                    }
                    prefs.accountSelectionOpened(b = true)
                    prefs.studentSignInStatus(true)
                    val broadcast = Intent(FINISH)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast)
                    startActivity(Intent(this, StudentActivity::class.java))
                    finish()
                }
            }.addOnFailureListener {
                Timber.e("Sign in failed: ${it.message}")
                binding.progressHorizontal.visibility = View.GONE
            }
    }

    private fun isFormValidated(): Boolean {
        var isValid = true
        if (binding.editTextStudentEmail.text?.isEmpty() == true) {
            isValid = false
            binding.textInputEmail.error = getString(R.string.field_cannot_be_empty)
        }

        if (binding.editTextStudentPassword.text?.isEmpty() == true) {
            isValid = false
            binding.textInputPassword.error = getString(R.string.field_cannot_be_empty)
        }

        return isValid
    }

}