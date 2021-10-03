package com.dev.gka.ktupascohub.activities.rep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.ProfileActivity
import com.dev.gka.ktupascohub.activities.SearchActivity
import com.dev.gka.ktupascohub.adapters.CourseRecyclerAdapter
import com.dev.gka.ktupascohub.databinding.ActivityRepBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Constants.COURSES
import com.dev.gka.ktupascohub.utilities.Helpers.firestoreData
import com.dev.gka.ktupascohub.utilities.Helpers.papers
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class RepActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rep)
        setSupportActionBar(binding.includedLayout.toolbarRep)

        firestore = FirebaseFirestore.getInstance()
        firestoreData(
            binding.includedLayout.content.rvRepQuestions,
            binding.includedLayout.content.indicatorRepLoad,
            firestore
        )
        binding.includedLayout.fabUpload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

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
            R.id.menu_notifications -> {
                true
            }
            R.id.menu_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    fun firestoreData() {
//        val courses = mutableListOf<Course>()
//        binding.includedLayout.content.indicatorRepLoad.visibility = View.VISIBLE
//        firestore.collection(COURSES)
//            .get()
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    binding.includedLayout.content.indicatorRepLoad.visibility = View.GONE
//                    for (documentSnapshot in it.result) {
//                        val course = Course(documentSnapshot.getString("lecturer"),
//                            documentSnapshot.getString("title"),
//                            documentSnapshot.getString("level"),
//                            documentSnapshot.getString("semester"), null)
//                        course.file = documentSnapshot.getString("file")
//                        courses.add(course)
//                    }
//                    binding.includedLayout.content.rvRepQuestions.apply {
//                        adapter = CourseRecyclerAdapter(CourseRecyclerAdapter.OnClickListener {
//
//                        }, courses)
//                        layoutManager = LinearLayoutManager(
//                            context,
//                            LinearLayoutManager.VERTICAL,
//                            false
//                        )
//                    }
//                }
//            }.addOnFailureListener {
//                Timber.e("Data load failed: ${it.message}")
//            }
//    }
}