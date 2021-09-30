package com.dev.gka.ktupascohub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityRepBinding

class RepActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rep)
    }
}