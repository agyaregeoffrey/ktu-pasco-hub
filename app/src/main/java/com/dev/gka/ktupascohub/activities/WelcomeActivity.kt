package com.dev.gka.ktupascohub.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.rep.RepSignInActivity
import com.dev.gka.ktupascohub.databinding.ActivityWelcomeBinding
import com.dev.gka.ktupascohub.utilities.PrefManager

class WelcomeActivity : BaseActivity() {
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityWelcomeBinding>(this, R.layout.activity_welcome)

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val pref = PrefManager.getInstance(this)
            val isOpened = pref.hasAccountSelectionRun()
            val rep = pref.hasRepSignedIn()
            val stud = pref.hasStudentSignedIn()
            if (isOpened) {
                if (rep) {
                    val intent = Intent(this, RepSignInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (stud) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, AccountSelectionActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}