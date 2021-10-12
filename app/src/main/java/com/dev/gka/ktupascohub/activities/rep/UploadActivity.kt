package com.dev.gka.ktupascohub.activities.rep

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.Preview
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.BaseActivity
import com.dev.gka.ktupascohub.databinding.ActivityUploadBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.models.PushNotification
import com.dev.gka.ktupascohub.utilities.Constants.COURSES
import com.dev.gka.ktupascohub.utilities.Constants.FIRST_YEAR
import com.dev.gka.ktupascohub.utilities.Constants.QUESTIONS
import com.dev.gka.ktupascohub.utilities.Constants.SECOND_YEAR
import com.dev.gka.ktupascohub.utilities.Constants.STORAGE_PATH
import com.dev.gka.ktupascohub.utilities.Constants.THIRD_YEAR
import com.dev.gka.ktupascohub.utilities.Constants.TOPIC
import com.dev.gka.ktupascohub.utilities.Helpers.courses
import com.dev.gka.ktupascohub.utilities.Helpers.showSnack
import com.dev.gka.ktupascohub.utilities.RequestConfirmationListener
import com.dev.gka.ktupascohub.utilities.RetrofitInstance
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class UploadActivity : BaseActivity(), RequestConfirmationListener {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var fireStoreDatabase: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var title: String
    private lateinit var lecturer: String
    private lateinit var level: String
    private lateinit var year: String
    private lateinit var semester: String


    private var questionFileUri: Uri? = null
    private lateinit var questionFileName: String

    private var solutionFileUri: Uri? = null
    private lateinit var solutionFileName: String

    private lateinit var fileNameWithoutExtension: String

    private var questionSelected = false
    private var solutionSelected = false

    private val startForResultQuestion =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                questionFileUri = result.data!!.data!!
                questionUrlToString(result.data!!, binding.buttonAttachQuestion)
                questionSelected = true
            }
        }

    private val startForResultSolution =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                solutionFileUri = result.data!!.data!!
                solutionUrlToString(result.data!!, binding.buttonAttachSolution)
                solutionSelected = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)
        setSupportActionBar(binding.toolbarUpload)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fireStoreDatabase = Firebase.firestore
        firebaseStorage = Firebase.storage

        initializeDropDown()

        binding.lecturerDropdown.setOnItemClickListener { _, _, position, _ ->
            for (i in 0..courses.size) {
                binding.courseSelection.editText?.setText(courses[position].title)
                binding.levelSelection.editText?.setText(courses[position].level)
                binding.semesterSelection.editText?.setText(courses[position].semester)
            }
        }

        binding.buttonAttachQuestion.setOnClickListener {
            selectQuestion()
        }

        binding.buttonAttachSolution.setOnClickListener {
            selectSolution()
        }

        binding.buttonUpload.setOnClickListener {
            title = binding.courseSelection.editText?.text.toString()
            lecturer = binding.lecturerSelection.editText?.text.toString()
            level = binding.levelSelection.editText?.text.toString()
            year = binding.yearSelection.editText?.text.toString()
            semester = binding.semesterSelection.editText?.text.toString()

            if (isFormValidated(it)) {
                Preview.newInstance(
                    title, lecturer, year, level, semester, questionFileName, solutionFileName
                ).show(supportFragmentManager, "")
            }

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

    // Dropdown initializations
    private fun initializeDropDown() {
        val titles: MutableList<String> = mutableListOf()
        val lecturers: MutableList<String> = mutableListOf()
        val levels: MutableList<String> = mutableListOf("300", "200", "100")
        val semesters: MutableList<String> = mutableListOf("First", "Second")
        val years: MutableList<String> =
            mutableListOf("2015", "2016", "2017", "2018", "2019", "2020")

        for (course in courses) {
            titles.add(course.title!!)
            lecturers.add(course.lecturer!!)
        }

        val lecturerAdapter =
            ArrayAdapter(applicationContext, R.layout.drop_down_list_item, lecturers)
        val titleAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_list_item, titles)
        val levelAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_list_item, levels)
        val yearAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_list_item, years)
        val semesterAdapter =
            ArrayAdapter(applicationContext, R.layout.drop_down_list_item, semesters)


        binding.lecturerDropdown.setAdapter(lecturerAdapter)
        binding.courseDropdown.setAdapter(titleAdapter)
        binding.levelDropdown.setAdapter(levelAdapter)
        binding.yearDropdown.setAdapter(yearAdapter)
        binding.semesterDropdown.setAdapter(semesterAdapter)
    }

    private fun selectQuestion() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startForResultQuestion.launch(Intent.createChooser(intent, getString(R.string.select_question)))
    }


    private fun selectSolution() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startForResultSolution.launch(Intent.createChooser(intent, getString(R.string.select_solution)))
    }

    private fun questionUrlToString(returnIntent: Intent, button: MaterialButton) {
        returnIntent.data?.let { uri ->
            contentResolver.query(uri, null, null, null, null)
        }?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            button.text = cursor.getString(index)
            questionFileName = cursor.getString(index)
            fileNameWithoutExtension = File(questionFileName).nameWithoutExtension
        }
    }

    private fun solutionUrlToString(returnIntent: Intent, button: MaterialButton) {
        returnIntent.data?.let { uri ->
            contentResolver.query(uri, null, null, null, null)
        }?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            button.text = cursor.getString(index)
            solutionFileName = cursor.getString(index)
            fileNameWithoutExtension = File(solutionFileName).nameWithoutExtension
        }
    }
    private fun isFormValidated(view: View): Boolean {
        var isValid = true

        if (binding.lecturerSelection.editText?.text?.isEmpty() == true
            || binding.courseSelection.editText?.text?.isEmpty() == true
            || binding.yearSelection.editText?.text?.isEmpty() == true
            || binding.semesterSelection.editText?.text?.isEmpty() == true
        ) {
            isValid = false
            showSnack(view, "All fields required")
        }

        if (!questionSelected) {
            isValid = false
            showSnack(view, "Select a file")
        }

        return isValid
    }

    private fun uploadFile(
        title: String,
        lecturer: String,
        level: String,
        semester: String,
        year: String,
        path: String,
    ) {
        binding.buttonUpload.isEnabled = false
        binding.indicatorUpload.visibility = View.VISIBLE
        val storageReference: StorageReference =
            firebaseStorage
                .reference
                .child(
                    "$STORAGE_PATH/$questionFileName"
                )
        questionFileUri?.let {
            storageReference.putFile(it)
                .addOnSuccessListener { taskSnapShot ->
                    // Add file url to past question details to enable user download
                    val uri: Task<Uri> = taskSnapShot.storage.downloadUrl
                    while (!uri.isComplete);
                    var sUri: Uri? = null
                    if (solutionFileUri != null)
                        sUri = uploadSolution()
                    val questionUri: Uri? = uri.result
                    val course = Course(
                        lecturer,
                        title,
                        level,
                        year,
                        semester,
                        questionUri.toString(),
                        sUri.toString()
                    )
                    fireStoreDatabase
                        .collection(QUESTIONS)
                        .document(COURSES)
                        .collection(path)
                        .add(course)
                    binding.buttonUpload.isEnabled = true
                    binding.indicatorUpload.visibility = View.GONE
                    val intent = Intent(this, RepActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener { e ->
                    binding.buttonUpload.isEnabled = true
                    binding.indicatorUpload.visibility = View.GONE
                    Timber.e("File upload failed: ${e.message}")
                }
        }
    }

    private fun uploadSolution(): Uri? {
        var solutionUri: Uri? = null
        val storageReference: StorageReference =
            firebaseStorage
                .reference
                .child(
                    "$STORAGE_PATH/$solutionFileName"
                )
        solutionFileUri?.let { uri ->
            storageReference.putFile(uri)
                .addOnSuccessListener { taskSnapShot ->
                    // Add file url to past question details to enable user download
                    val task: Task<Uri> = taskSnapShot.storage.downloadUrl
                    while (!task.isComplete);
                    val url: Uri? = task.result
                    solutionUri = url
                }.addOnFailureListener {
                    Timber.e("Solution upload failed: ${it.message}")
                }
        }
        return solutionUri
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Timber.d("Response: ${Gson().toJson(response)}")
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
        }

    private fun collectionPath(level: Int): String {
        return when (level) {
            100 -> FIRST_YEAR
            200 -> SECOND_YEAR
            300 -> THIRD_YEAR
            else -> "Not found"
        }
    }

    override fun onConfirmed() {
        val fbPath = collectionPath(level.toInt())
        uploadFile(title, lecturer, level, semester, year, fbPath)
        PushNotification(Course("New Past Question Uploaded", title, null,
            null, null, null, null), TOPIC).also { push ->
            sendNotification(push)
        }
    }
}