package com.dev.gka.ktupascohub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityStudentBinding

class StudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}