package com.dev.gka.ktupascohub.activities.rep

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.BaseActivity
import com.dev.gka.ktupascohub.databinding.ActivityUploadBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.models.PushNotification
import com.dev.gka.ktupascohub.utilities.Constants.COURSES
import com.dev.gka.ktupascohub.utilities.Constants.STORAGE_PATH
import com.dev.gka.ktupascohub.utilities.Constants.TOPIC
import com.dev.gka.ktupascohub.utilities.Helpers.courses
import com.dev.gka.ktupascohub.utilities.Helpers.showSnack
import com.dev.gka.ktupascohub.utilities.RetrofitInstance
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class UploadActivity : BaseActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var fireStoreDatabase: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var uri: Uri
    private var fileName: String = ""
    private var fileNameWithoutExtension: String = ""

    private var fileSelected = false

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uri = result.data!!.data!!
                fileUrlToString(result.data!!)
                fileSelected = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)
        setSupportActionBar(binding.toolbarUpload)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fireStoreDatabase = Firebase.firestore
        firebaseStorage = Firebase.storage
//        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        initializeDropDown()

        binding.buttonAttachFile.setOnClickListener {
            selectPdfFile()
        }

        binding.buttonUpload.setOnClickListener {
            val title = binding.courseSelection.editText?.text.toString()
            val lecturer = binding.lecturerSelection.editText?.text.toString()
            val year = binding.yearSelection.editText?.text.toString()
            val semester = binding.semesterSelection.editText?.text.toString()
            if (isFormValidated(it)) {
                uploadFile(title, lecturer, year, semester)
                PushNotification(Course("New Past Question Uploaded", title, null, null, null), TOPIC)
                    .also { push ->
                        sendNotification(push)
                    }
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

        for (course in courses) {
            titles.add(course.title!!)
            lecturers.add(course.lecturer!!)
        }

        val lecturerAdapter =
            ArrayAdapter(applicationContext, R.layout.drop_down_list_item, lecturers)
        val titleAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_list_item, titles)
        val levelAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_list_item, levels)
        val semesterAdapter =
            ArrayAdapter(applicationContext, R.layout.drop_down_list_item, semesters)


        binding.lecturerDropdown.setAdapter(lecturerAdapter)
        binding.courseDropdown.setAdapter(titleAdapter)
        binding.yearDropdown.setAdapter(levelAdapter)
        binding.semesterDropdown.setAdapter(semesterAdapter)
    }

    private fun selectPdfFile() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startForResult.launch(Intent.createChooser(intent, getString(R.string.select_pdf)))
    }

    private fun fileUrlToString(returnIntent: Intent) {
        returnIntent.data?.let { uri ->
            contentResolver.query(uri, null, null, null, null)
        }?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            binding.buttonAttachFile.visibility = View.GONE
            binding.groupFileName.visibility = View.VISIBLE
            binding.textViewFileName.text = cursor.getString(index)
            fileName = cursor.getString(index)
            fileNameWithoutExtension = File(fileName).nameWithoutExtension
        }
    }

    private fun isFormValidated(view: View): Boolean {
        var isValid = true

        if (binding.lecturerSelection.editText?.text?.isEmpty() == true
            || binding.courseSelection.editText?.text?.isEmpty() == true
            || binding.yearSelection.editText?.text?.isEmpty() == true
            || binding.semesterSelection.editText?.text?.isEmpty() == true) {
            isValid = false
            showSnack(view, "All fields required")
        }

        if (!fileSelected) {
            isValid = false
            showSnack(view, "Select a file")
        }

        return isValid
    }

    private fun uploadFile(title: String, lecturer: String, level: String, semester: String) {
        binding.buttonUpload.isEnabled = false
        binding.indicatorUpload.visibility = View.VISIBLE
        val storageReference: StorageReference =
            firebaseStorage
                .reference
                .child(
                    "$STORAGE_PATH/$fileName"
                )
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapShot ->
                // Add file url to past question details to enable user download
                val uri: Task<Uri> = taskSnapShot.storage.downloadUrl
                while (!uri.isComplete);
                val url: Uri? = uri.result
                val course = Course(
                    title,
                    lecturer,
                    level,
                    semester,
                    url.toString()
                )
                fireStoreDatabase
                    .collection(COURSES)
                    .add(course)
                binding.buttonUpload.isEnabled = true
                binding.indicatorUpload.visibility = View.GONE
                val intent = Intent(this, RepActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                binding.buttonUpload.isEnabled = true
                binding.indicatorUpload.visibility = View.GONE
                Timber.e("File upload failed: ${it.message}")
            }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Timber.d("Response: ${Gson().toJson(response)}")
            } else {
                Timber.d(response.errorBody().toString())
            }
        }catch (e: Exception) {
            Timber.e(e.toString())
        }
    }
}