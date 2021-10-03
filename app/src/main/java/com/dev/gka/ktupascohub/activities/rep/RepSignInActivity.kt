package com.dev.gka.ktupascohub.activities.rep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityRepSignInBinding
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class RepSignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepSignInBinding
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rep_sign_in)

        binding.buttonSignIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (isFormValidated()){
                createUserAccount(email, password)
//                signInUser(email, password)
            }
        }

        binding.editTextEmail.setOnKeyListener { _, _, _ ->
            if (isEmailValid(binding.editTextEmail.text)) {
                binding.textInputClassRepEmail.error = null
            }
            false
        }

        binding.editTextPassword.setOnKeyListener { _, _, _ ->
            if (isPasswordValid(binding.editTextPassword.text)) {
                binding.textInputClassRepPassword.error = null
            }
            false
        }
    }

    private fun createUserAccount(email: String, password: String) {
        binding.indicatorRep.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.indicatorRep.visibility = View.GONE
                    val user = auth.currentUser
                    val rep = user?.let {
                        Student(it.displayName, it.email, null, null)
                    }
                    PrefManager.getInstance(applicationContext).saveUserInfo(rep!!)
                    val intent = Intent(this, RepActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Timber.e("Rep sign up failed: ${it.message}")
                binding.indicatorRep.visibility = View.GONE
            }
    }

    private fun signInUser(email: String, password: String) {
        binding.indicatorRep.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.indicatorRep.visibility = View.GONE
                    val user = auth.currentUser
                    val rep = user?.let {
                        Student(it.displayName, it.email, null, null)
                    }
                    PrefManager.getInstance(applicationContext).saveUserInfo(rep!!)
                    val intent = Intent(this, RepActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Timber.e("Rep sign in failed: ${it.message}")
                binding.indicatorRep.visibility = View.GONE
            }
    }

    private fun isFormValidated(): Boolean {
        var isValid = true


        if (!isEmailValid(binding.editTextEmail.text)) {
            isValid = false
            binding.textInputClassRepEmail.error = getString(R.string.enter_a_valid_email)
        }

        if (!isPasswordValid(binding.editTextPassword.text)) {
            isValid = false
            binding.textInputClassRepPassword.error = getString(R.string.character_cannot_be_less_than_8)
        }

        return isValid
    }

    private fun isEmailValid(email: Editable?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches()
                && email != null
    }

    private fun isPasswordValid(password: Editable?): Boolean {
        return password != null && password.length >= 8
    }

    override fun onStart() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, RepActivity::class.java))
            finish()
        }
        super.onStart()
    }
}