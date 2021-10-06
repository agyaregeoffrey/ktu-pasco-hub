package com.dev.gka.ktupascohub.activities.rep

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.BaseActivity
import com.dev.gka.ktupascohub.databinding.ActivityRepSignInBinding
import com.dev.gka.ktupascohub.models.Rep
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Constants.COURSES
import com.dev.gka.ktupascohub.utilities.Constants.REPS
import com.dev.gka.ktupascohub.utilities.Helpers.hasNetworkConnected
import com.dev.gka.ktupascohub.utilities.Helpers.showSnack
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class RepSignInActivity : BaseActivity() {
    private lateinit var binding: ActivityRepSignInBinding
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var firestore: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rep_sign_in)

        firestore = FirebaseFirestore.getInstance()

        binding.buttonSignIn.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (isFormValidated()) {
                if (!hasNetworkConnected(this))
                    showSnack(it, "An active internet connection is required to sign in")
                else {
//                    createUserAccount(name, email, password)
                    signInUser(email, password)
                }
            }
        }

        binding.editTextName.setOnKeyListener { _, _, _ ->
            if (binding.editTextName.text?.isNotEmpty() == true) {
                binding.textInputName.error = null
            }
            false
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

    private fun createUserAccount(name: String, email: String, password: String) {
        binding.indicatorRep.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.indicatorRep.visibility = View.GONE
                    val user = auth.currentUser
                    if (user != null) {
                        nameToFirebase(name, user.uid)
                        val student = Student(name, user.email, null, null)
                        Timber.d("$student")

                        val prefs = PrefManager.getInstance(this)
                        prefs.welcomeActivityOpened(b = true)
                        prefs.saveUserInfo(student)
                        prefs.repSignUpStatus(b = true)
                    }
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
                    nameFromFirebase()
                }
            }.addOnFailureListener {
                Timber.e("Rep sign in failed: ${it.message}")
                binding.indicatorRep.visibility = View.GONE
            }
    }

    private fun nameToFirebase(name: String, uid: String) {
        val rep = Rep(name, uid)
        firestore.collection(COURSES)
            .document(REPS)
            .collection(uid)
            .add(rep)
    }

    private fun nameFromFirebase() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection(COURSES)
                .document(REPS)
                .collection(user.uid)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result.documents[0]
                        val name = document.getString("name")
                        val student = Student(name, user.email, null, null)
                        Timber.d("$student")

                        val prefs = PrefManager.getInstance(this)
                        prefs.welcomeActivityOpened(true)
                        prefs.saveUserInfo(student)
                        prefs.repSignUpStatus(true)
                        binding.indicatorRep.visibility = View.GONE
                        val intent = Intent(this, RepActivity::class.java)
                        startActivity(intent)
                        finish()
                        Timber.d("Name: $name")
                    }
                }.addOnFailureListener {
                    Timber.d("Rep : $it")
                }
        }
    }

    private fun isFormValidated(): Boolean {
        var isValid = true

        if (binding.editTextName.text?.isEmpty() == true) {
            isValid = false
            binding.textInputName.error = "Field cannot be empty"
        }

        if (!isEmailValid(binding.editTextEmail.text)) {
            isValid = false
            binding.textInputClassRepEmail.error = getString(R.string.enter_a_valid_email)
        }

        if (!isPasswordValid(binding.editTextPassword.text)) {
            isValid = false
            binding.textInputClassRepPassword.error =
                getString(R.string.character_cannot_be_less_than_8)
        }

        return isValid
    }

    private fun isNameValid(name: String): Boolean {
        return false
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