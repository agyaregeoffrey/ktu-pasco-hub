package com.dev.gka.ktupascohub.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityStudentBinding
import com.dev.gka.ktupascohub.utilities.Constants.TOPIC
import com.dev.gka.ktupascohub.utilities.Helpers.firestoreData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class StudentActivity : BaseActivity() {
    private lateinit var binding: ActivityStudentBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student)
        setSupportActionBar(binding.toolbarStudent)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        firestore = FirebaseFirestore.getInstance()

    }

    private fun initFirebaseData() {
        firestoreData(
            this.applicationContext,
            binding.rvStudent,
            binding.indicatorStudent,
            firestore
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_refresh -> {
                initFirebaseData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}