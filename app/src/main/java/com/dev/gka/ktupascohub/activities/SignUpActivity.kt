package com.dev.gka.ktupascohub.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivitySignUpBinding
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Helpers.isEmailValid
import com.dev.gka.ktupascohub.utilities.Helpers.isPasswordValid
import com.dev.gka.ktupascohub.utilities.Helpers.nameToFirebase
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        binding.buttonSignUp.setOnClickListener {
            val name = binding.editTextAccountName.text.toString()
            val email = binding.editTextAccountEmail.text.toString()
            val password = binding.editTextAccountPassword.text.toString()

            if (isFormValidated()) {
                createAccount(name, email, password)
            }
        }
    }

    private fun createAccount(name: String, email: String, password: String) {
        binding.indicatorCreateAccount.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        binding.indicatorCreateAccount.visibility = View.GONE
                        nameToFirebase(fireStore, name, user.uid)
                        val student = Student(name, email, null, null)
                        val prefs = PrefManager.getInstance(this)
                        prefs.saveUserInfo(student)
                        prefs.accountSelectionOpened(b = true)
                        prefs.studentSignInStatus(b = true)
                        startActivity(Intent(this, LevelSelectionActivity::class.java))
                        finish()
                    }
                }
            }.addOnFailureListener {
                binding.indicatorCreateAccount.visibility = View.GONE
            }
    }

    private fun isFormValidated(): Boolean {
        var isValid = true

        if (binding.editTextAccountName.text?.isEmpty() == true) {
            isValid = false
            binding.editTextAccountName.error = getString(R.string.field_cannot_be_empty)
        }

        if (binding.editTextAccountEmail.text?.contains("rep", true) == true) {
            isValid = false
            binding.textInputAccountEmail.error = getString(R.string.enter_a_valid_email)
        }

        if (!isEmailValid(binding.editTextAccountEmail.text, binding.editTextAccountEmail)) {
            isValid = false
            binding.textInputAccountEmail.error = getString(R.string.enter_a_valid_email)
        }

        if (!isPasswordValid(binding.editTextAccountPassword.text)) {
            isValid = false
            binding.textInputAccountPassword.error =
                getString(R.string.character_cannot_be_less_than_8)
        }

        return isValid
    }
}