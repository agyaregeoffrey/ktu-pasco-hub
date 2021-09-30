package com.dev.gka.ktupascohub.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.MainActivity
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityAccountSelectionBinding

class AccountSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_selection)

        binding.buttonClassRep.setOnClickListener {
            val repIntent = Intent(this, RepActivity::class.java)
            startActivity(repIntent)
        }

        binding.buttonStudent.setOnClickListener {
            val studentIntent = Intent(this, MainActivity::class.java)
            startActivity(studentIntent)
        }
    }
}