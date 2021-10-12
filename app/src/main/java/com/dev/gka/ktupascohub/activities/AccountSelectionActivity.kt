package com.dev.gka.ktupascohub.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.rep.RepSignInActivity
import com.dev.gka.ktupascohub.activities.rep.RepActivity
import com.dev.gka.ktupascohub.databinding.ActivityAccountSelectionBinding
import com.dev.gka.ktupascohub.utilities.Constants.FINISH
import com.dev.gka.ktupascohub.utilities.PrefManager

class AccountSelectionActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountSelectionBinding

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == FINISH) {
                finish()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_selection)

        binding.buttonClassRep.setOnClickListener {
            val repIntent = Intent(this, RepSignInActivity::class.java)
            startActivity(repIntent)
        }

        binding.buttonStudent.setOnClickListener {
            val studentIntent = Intent(this, MainActivity::class.java)
            startActivity(studentIntent)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(FINISH))
    }

    override fun onStart() {
        val prefs = PrefManager.getInstance(this)
        val account = prefs.hasAccountSelectionRun()
        val rep = prefs.hasRepSignedIn()
        val stud = prefs.hasStudentSignedIn()
        if (account) {
            if (rep) {
                startActivity(Intent(this, RepSignInActivity::class.java))
                finish()
            } else if (stud) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }
}