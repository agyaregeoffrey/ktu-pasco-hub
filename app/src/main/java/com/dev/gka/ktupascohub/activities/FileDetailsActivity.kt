package com.dev.gka.ktupascohub.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ActivityFileDetailsBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Constants.LECTURER
import com.dev.gka.ktupascohub.utilities.Constants.LEVEL
import com.dev.gka.ktupascohub.utilities.Constants.QUESTION_URL
import com.dev.gka.ktupascohub.utilities.Constants.SEMESTER
import com.dev.gka.ktupascohub.utilities.Constants.SOLUTION_URL
import com.dev.gka.ktupascohub.utilities.Constants.TITLE
import com.dev.gka.ktupascohub.utilities.Constants.YEAR
import com.dev.gka.ktupascohub.utilities.Helpers.downloadFile
import com.dev.gka.ktupascohub.utilities.Helpers.hasNetworkConnected
import com.dev.gka.ktupascohub.utilities.Helpers.requestStoragePermission
import com.dev.gka.ktupascohub.utilities.Helpers.showSnack
import com.dev.gka.ktupascohub.utilities.PermissionsCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class FileDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileDetailsBinding
    private lateinit var course: Course
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_details)
        setSupportActionBar(binding.toolbarDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            binding.buttonDownloadQuestion.isEnabled = true
            val lecturer = bundle.getString(LECTURER)
            val title = bundle.getString(TITLE)
            val level = bundle.getString(LEVEL)
            val year = bundle.getString(YEAR)
            val semester = bundle.getString(SEMESTER)
            val question = bundle.getString(QUESTION_URL)
            val solution = bundle.getString(SOLUTION_URL)

            course = Course(
                lecturer, title, level,
                year, semester, question, solution,
            )

            if (course.solution == "null") {
                binding.buttonDownloadSolution.text = getString(R.string.no_solution_available)
                binding.buttonDownloadSolution.isEnabled = false
            } else {
                binding.buttonDownloadSolution.setOnClickListener {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.download_solution_title))
                        .setMessage(getString(R.string.solution_disclaimer))
                        .setPositiveButton(getString(R.string.download_file)) { _, _ ->
                            downloadSolution(it)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                }
            }


            Timber.d("Det Solution: ${course.solution}")

            binding.editTextTitle.setText(title)
            binding.editTextLecturer.setText(lecturer)
            binding.editTextLevel.setText(level)
            binding.editTextYear.setText(year)
            binding.editTextSemester.setText(semester)
        }

        binding.buttonDownloadQuestion.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.download_file))
                .setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(R.string.download_file)) { _, _ ->
                    downloadQuestion(it)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
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


    private fun downloadQuestion(view: View) {
        requestStoragePermission(applicationContext, object : PermissionsCallback {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPermissionRequest(granted: Boolean) {
                if (granted) {
                    if (hasNetworkConnected(applicationContext)) {
                        showSnack(
                            view,
                            "Downloading Question ${course.title}"
                        )
                        downloadFile(
                            applicationContext,
                            course.title!!,
                            course.question!!
                        )
                    } else showSnack(
                        view,
                        "An active internet connection is required to download file"
                    )
                } else
                    showSnack(
                        view,
                        "Storage permission is required to download file."
                    )
            }

        })
    }

    private fun downloadSolution(view: View) {
        requestStoragePermission(applicationContext, object : PermissionsCallback {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPermissionRequest(granted: Boolean) {
                if (granted) {
                    if (hasNetworkConnected(applicationContext)) {
                        showSnack(
                            view,
                            "Downloading Solution ${course.title}"
                        )
                        downloadFile(
                            applicationContext,
                            course.title!!,
                            course.solution!!
                        )
                    } else showSnack(
                        view,
                        "An active internet connection is required to download file"
                    )
                } else
                    showSnack(
                        view,
                        "Storage permission is required to download file."
                    )
            }

        })
    }
}