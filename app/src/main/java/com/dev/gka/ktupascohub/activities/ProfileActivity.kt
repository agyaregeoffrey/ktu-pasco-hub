package com.dev.gka.ktupascohub.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.fragments.EditLevel
import com.dev.gka.ktupascohub.databinding.ActivityProfileBinding
import com.dev.gka.ktupascohub.utilities.Constants.LOG_OUT
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.dev.gka.ktupascohub.utilities.RequestChangeLevelListener
import com.dev.gka.ktupascohub.utilities.UploadConfirmationListener
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : BaseActivity(), RequestChangeLevelListener {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeProfileDetails()
        binding.buttonSignOut.setOnClickListener {
            signOutBroadcastReceiver()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeProfileDetails() {
        val pref = PrefManager.getInstance(applicationContext)
        binding.textDisplayName.text = pref.getDisplayName() ?: "Student Name"
        binding.textEmail.text = pref.getEmail()
        binding.textLevel.text = getString(R.string.level, pref.getLevel())

        val initials = StringBuilder()

        val savedName: String = pref.getDisplayName() ?: "Student Name"
        val names = savedName.split(" ")
        val takenNames = names.take(2)
        for (name in takenNames) {
                initials.append(name[0].uppercaseChar())
            }
        binding.imageProfile.visibility = View.GONE

        binding.textNameInitials.apply {
            visibility = View.VISIBLE
            text = initials.toString()
        }

        binding.imageEditLevel.setOnClickListener {
            EditLevel.newInstance().show(supportFragmentManager, "")
        }

    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        val preferences = getSharedPreferences(applicationContext.packageName, Activity.MODE_PRIVATE)
        preferences.edit().clear().apply()

        val intent = Intent(this, AccountSelectionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun signOutBroadcastReceiver() {
        FirebaseAuth.getInstance().signOut()

        val preferences = getSharedPreferences(applicationContext.packageName, Activity.MODE_PRIVATE)
        preferences.edit().clear().apply()

        val intent = Intent(LOG_OUT)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onChangeRequest(newLevel: String) {
        PrefManager.getInstance(applicationContext).studentLevel(newLevel)
        binding.textLevel.text = getString(R.string.level, newLevel)
    }
}