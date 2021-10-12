package com.dev.gka.ktupascohub.activities.rep

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.BaseActivity
import com.dev.gka.ktupascohub.activities.LevelSelectionActivity
import com.dev.gka.ktupascohub.databinding.ActivityRepSignInBinding
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Constants.FINISH
import com.dev.gka.ktupascohub.utilities.Helpers.hasNetworkConnected
import com.dev.gka.ktupascohub.utilities.Helpers.isEmailValid
import com.dev.gka.ktupascohub.utilities.Helpers.isPasswordValid
import com.dev.gka.ktupascohub.utilities.Helpers.nameFromFirebase
import com.dev.gka.ktupascohub.utilities.Helpers.nameToFirebase
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
                    showSnack(it, getString(R.string.active_internet_needed))
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
            if (isEmailValid(binding.editTextEmail.text, binding.editTextEmail)) {
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
                        nameToFirebase(firestore, name, user.uid)
                        val student = Student(name, user.email, null, null)
                        Timber.d("$student")

                        val prefs = PrefManager.getInstance(this)
                        prefs.saveUserInfo(student)
                        prefs.accountSelectionOpened(b = true)
                        prefs.repSignUpStatus(b = true)
                    }
                    val broadcast = Intent(FINISH)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast)
                    val intent = Intent(this, LevelSelectionActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showSnack(binding.imageView, getString(R.string.sign_up_failed))
                }
            }.addOnFailureListener {
                showSnack(binding.imageView, getString(R.string.sign_up_failed))
                Timber.e("Rep sign up failed: ${it.message}")
                binding.indicatorRep.visibility = View.GONE
            }
    }

    private fun signInUser(email: String, password: String) {
        binding.indicatorRep.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                nameFromFirebase(auth, firestore, this)
                if (task.isSuccessful) {
                    val prefs = PrefManager.getInstance(this)
                    prefs.accountSelectionOpened(b = true)
                    prefs.repSignUpStatus(b = true)
                    val broadcast = Intent(FINISH)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast)
                    val intent = Intent(this, LevelSelectionActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showSnack(binding.imageView, getString(R.string.sign_failed))
                }
            }.addOnFailureListener {
                showSnack(binding.imageView, getString(R.string.sign_failed))
                Timber.e("Rep sign in failed: $it")
                binding.indicatorRep.visibility = View.GONE
            }
    }

    private fun isFormValidated(): Boolean {
        var isValid = true

        if (binding.editTextName.text?.isEmpty() == true) {
            isValid = false
            binding.textInputName.error = getString(R.string.field_cannot_be_empty)
        }

        if (!isEmailValid(binding.editTextEmail.text, binding.editTextEmail)) {
            isValid = false
            binding.textInputClassRepEmail.error = getString(R.string.enter_a_valid_email)
        }

        if (binding.editTextEmail.text?.contains("rep", true) != true) {
            isValid = false
            binding.textInputClassRepEmail.error = getString(R.string.admin_account_required)
        }

        if (!isPasswordValid(binding.editTextPassword.text)) {
            isValid = false
            binding.textInputClassRepPassword.error =
                getString(R.string.field_cannot_be_empty)
        }

        return isValid
    }


    override fun onStart() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, LevelSelectionActivity::class.java))
            finish()
        }
        super.onStart()
    }
}