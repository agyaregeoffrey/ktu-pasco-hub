package com.dev.gka.ktupascohub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.adapters.CourseRecyclerAdapter
import com.dev.gka.ktupascohub.databinding.ActivitySearchBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Constants
import com.dev.gka.ktupascohub.utilities.Helpers.firestoreData
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        setSupportActionBar(binding.searchToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firestore = FirebaseFirestore.getInstance()
        firestoreData(binding.rvSearch, binding.indicatorSearch, firestore)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}