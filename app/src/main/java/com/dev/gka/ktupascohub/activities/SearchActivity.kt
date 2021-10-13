package com.dev.gka.ktupascohub.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.adapters.CourseRecyclerAdapter
import com.dev.gka.ktupascohub.databinding.ActivitySearchBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Helpers
import com.dev.gka.ktupascohub.utilities.Helpers.collectionPath
import com.dev.gka.ktupascohub.utilities.Helpers.firestoreData
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var courses: MutableList<Course>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        setSupportActionBar(binding.searchToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firestore = FirebaseFirestore.getInstance()
        val level = collectionPath(PrefManager.getInstance(this).getStudentLevel()?.toInt())

        courses = firestoreData(
            this.applicationContext,
            binding.rvSearch,
            binding.groupSearch,
            binding.indicatorSearch,
            firestore, level
        )


        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                search(s.toString())
            }

        })
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

    private fun search(s: String) {
        val results = mutableListOf<Course>()
        for (course in courses) {
            if (course.lecturer?.lowercase()?.contains(s) == true
                || course.title?.lowercase()?.contains(s) == true
                || course.level?.lowercase()?.contains(s) == true
                || course.year?.lowercase()?.contains(s) == true
                || course.semester?.lowercase()?.contains(s) == true
            ) {
                results.add(course)
            }
        }

        if (results.isEmpty()) {
            binding.textSearchStatus.visibility = View.VISIBLE
            binding.rvSearch.visibility = View.GONE
        } else {
            binding.textSearchStatus.visibility = View.GONE
            binding.rvSearch.visibility = View.VISIBLE
            binding.rvSearch.apply {
                adapter = CourseRecyclerAdapter(CourseRecyclerAdapter.OnClickListener { course ->
                    Helpers.downloadFile(
                        applicationContext,
                        course.title!!,
                        course.question!!
                    )
                }, results)
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }
        }
    }
}