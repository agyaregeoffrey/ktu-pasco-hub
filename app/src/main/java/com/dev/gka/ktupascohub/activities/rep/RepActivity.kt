package com.dev.gka.ktupascohub.activities.rep

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
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
import timber.log.Timber

class RepActivity : BaseActivity() {
    private lateinit var binding: ActivityRepBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rep)
        setSupportActionBar(binding.includedLayout.toolbarRep)

        firestore = FirebaseFirestore.getInstance()


        initFirebaseData()

        binding.includedLayout.fabUpload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        initFirebaseData()
        super.onResume()
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

    private fun initFirebaseData() {
        val level = collectionPath(PrefManager.getInstance(applicationContext).getStudentLevel()?.toInt())
       firestoreData(
            applicationContext,
            binding.includedLayout.content.rvRepQuestions,
            binding.includedLayout.content.groupRep,
            binding.includedLayout.content.indicatorRepLoad,
            firestore, level
        )
    }

}