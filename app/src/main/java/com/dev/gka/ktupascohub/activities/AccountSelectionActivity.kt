package com.dev.gka.ktupascohub.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.rep.RepSignInActivity
import com.dev.gka.ktupascohub.activities.rep.RepActivity
import com.dev.gka.ktupascohub.databinding.ActivityAccountSelectionBinding
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.auth.FirebaseAuth

class AccountSelectionActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_selection)

        binding.buttonClassRep.setOnClickListener {
            val repIntent = Intent(this, RepSignInActivity::class.java)
            startActivity(repIntent)
            finish()
        }

        binding.buttonStudent.setOnClickListener {
            val studentIntent = Intent(this, MainActivity::class.java)
            startActivity(studentIntent)
            finish()
        }
    }
}