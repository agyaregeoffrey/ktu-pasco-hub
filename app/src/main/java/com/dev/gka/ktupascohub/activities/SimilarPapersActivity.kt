package com.dev.gka.ktupascohub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivitySimilarPapersBinding
import com.dev.gka.ktupascohub.utilities.Constants.LECTURER
import com.dev.gka.ktupascohub.utilities.Helpers.collectionPath
import com.dev.gka.ktupascohub.utilities.Helpers.showSnack
import com.dev.gka.ktupascohub.utilities.Helpers.similarPapers
import com.dev.gka.ktupascohub.utilities.PrefManager
import com.google.firebase.firestore.FirebaseFirestore

class SimilarPapersActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimilarPapersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_similar_papers)
        setSupportActionBar(binding.toolbarSimilar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val firestore = FirebaseFirestore.getInstance()
        val level = collectionPath(PrefManager.getInstance(this).getStudentLevel()?.toInt())

        val intent = intent
        val lecturer = intent.getStringExtra(LECTURER)

        similarPapers(
            applicationContext,
            binding.rvSimilar,
            binding.indicatorSimilar,
            firestore, level,
            lecturer!!
        )
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