package com.dev.gka.ktupascohub.activities.rep

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.BaseActivity
import com.dev.gka.ktupascohub.activities.ProfileActivity
import com.dev.gka.ktupascohub.activities.SearchActivity
import com.dev.gka.ktupascohub.activities.SimilarPapersActivity
import com.dev.gka.ktupascohub.databinding.ActivityRepBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Constants.LECTURER
import com.dev.gka.ktupascohub.utilities.Helpers.collectionPath
import com.dev.gka.ktupascohub.utilities.Helpers.firestoreData
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.firestore.FirebaseFirestore

class RepActivity : BaseActivity(), View.OnCreateContextMenuListener {
    private lateinit var binding: ActivityRepBinding
    private lateinit var firestore: FirebaseFirestore

    private var level: String? = null
    private lateinit var courses: MutableList<Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rep)
        setSupportActionBar(binding.includedLayout.toolbarRep)

        firestore = FirebaseFirestore.getInstance()
        level = collectionPath(PrefManager.getInstance(this).getStudentLevel()?.toInt())

        initFirebaseData()

        registerForContextMenu(binding.includedLayout.content.rvRepQuestions)

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
            R.id.menu_refresh -> {
                initFirebaseData()
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_similar -> {
                val intent = Intent(this, SimilarPapersActivity::class.java)
                intent.putExtra(LECTURER, courses[item.groupId].lecturer)
                startActivity(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun initFirebaseData() {
        courses = firestoreData(
            this.applicationContext,
            binding.includedLayout.content.rvRepQuestions,
            binding.includedLayout.content.groupRep,
            binding.includedLayout.content.indicatorRepLoad,
            firestore, level!!
        )
    }
}