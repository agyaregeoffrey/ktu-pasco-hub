package com.dev.gka.ktupascohub.utilities


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Editable
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.activities.FileDetailsActivity
import com.dev.gka.ktupascohub.adapters.CourseRecyclerAdapter
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.models.Rep
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Constants.COURSES
import com.dev.gka.ktupascohub.utilities.Constants.LECTURER
import com.dev.gka.ktupascohub.utilities.Constants.LEVEL
import com.dev.gka.ktupascohub.utilities.Constants.QUESTIONS
import com.dev.gka.ktupascohub.utilities.Constants.QUESTION_URL
import com.dev.gka.ktupascohub.utilities.Constants.SEMESTER
import com.dev.gka.ktupascohub.utilities.Constants.SOLUTION_URL
import com.dev.gka.ktupascohub.utilities.Constants.STUDENTS
import com.dev.gka.ktupascohub.utilities.Constants.TITLE
import com.dev.gka.ktupascohub.utilities.Constants.YEAR
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import timber.log.Timber

object Helpers {
    fun showSnack(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    // Dummy data
//    val papers = listOf(
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "First", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "First", "Semester 2", null),
//        Course("Solomon Anab", "Data Structures & Algorithm", "2020", "First","Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Solomon Anab", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
//    )

    val courses = listOf(
        Course(
            "Florence Agyeiwaa",
            "CSD 233 Social, Legal & Ethical Issues in Computing",
            "300",
            "2020",
            "First",
            null,
            null,
        ),
        Course(
            "Sefakor Awurama Adabunu",
            "CSD 307 Operations Research 1",
            "300",
            "2019",
            "First",
            null,
            null,
        ),
        Course("Martin Offei", "CSD 313 Server Concepts", "300", "2018", "First", null, null),
        Course(
            "Bright Anibreka",
            "CSD 317 System Administration",
            "300",
            "2017",
            "First",
            null,
            null,
        ),
        Course(
            "Benjamin Kwoffie",
            "CSD 319 IT & the Contemporary Manager (Entrepreneurship)",
            "300",
            "2019",
            "First",
            null,
            null,
        )
    )

    fun firestoreData(
        context: Context,
        recyclerView: RecyclerView,
        group: View,
        progressIndicator: LinearProgressIndicator,
        firestore: FirebaseFirestore,
        level: String
    ): MutableList<Course> {
        val courses = mutableListOf<Course>()
        progressIndicator.visibility = View.VISIBLE
        firestore.collection(QUESTIONS)
            .document(COURSES)
            .collection(level)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressIndicator.visibility = View.GONE
                    for (documentSnapshot in it.result) {
                        val course = Course(
                            documentSnapshot.getString(LECTURER),
                            documentSnapshot.getString(TITLE),
                            documentSnapshot.getString(LEVEL),
                            documentSnapshot.getString(YEAR),
                            documentSnapshot.getString(SEMESTER),
                            documentSnapshot.getString(QUESTION_URL),
                            documentSnapshot.getString(SOLUTION_URL),
                        )
                        Timber.d("Solution URL: ${course.solution}")
                        courses.add(course)
                    }
                    if (courses.isEmpty()) {
                        group.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        group.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.apply {
                            adapter =
                                CourseRecyclerAdapter(CourseRecyclerAdapter.OnClickListener { course ->
                                    courseBundle(context, course)
                                }, courses)
                            layoutManager = LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        }
                    }
                }
            }.addOnFailureListener {
                Timber.e("Data load failed: ${it.message}")
            }
        return courses
    }

    fun downloadFile(context: Context, name: String, url: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadRequest = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(name)
            .setDescription("Downloading")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
        downloadManager.enqueue(downloadRequest)
    }


    fun requestStoragePermission(context: Context, callback: PermissionsCallback) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    callback.onPermissionRequest(granted = true)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    callback.onPermissionRequest(granted = false)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasNetworkConnected(context: Context): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetwork
        manager?.getNetworkCapabilities(network).also {
            return it != null && it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }

    fun nameToFirebase(firestore: FirebaseFirestore, name: String, uid: String) {
        val rep = Rep(name, uid)
        firestore.collection(Constants.USERS)
            .document(STUDENTS)
            .collection(uid)
            .add(rep)
    }

    fun nameFromFirebase(
        auth: FirebaseAuth, firestore: FirebaseFirestore, context: Context
    ): Boolean {
        var isComplete = false
        val user = auth.currentUser
        var name: String? = ""
        if (user != null) {
            firestore.collection(Constants.USERS)
                .document(STUDENTS)
                .collection(user.uid)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            name = document.getString("name")
                        }
                        val student = Student(name, user.email, null, null)
                        Timber.d("$student")

                        PrefManager.getInstance(context).saveUserInfo(student)
                        isComplete = true
                    }
                }.addOnFailureListener {
                    Timber.d("Rep : $it")
                    isComplete = false
                }
        }
        return isComplete
    }

    private fun courseBundle(context: Context, course: Course) {
        val bundle = bundleOf(
            TITLE to course.title,
            LECTURER to course.lecturer,
            LEVEL to course.level,
            YEAR to course.year,
            SEMESTER to course.semester,
            QUESTION_URL to course.question,
            SOLUTION_URL to course.solution
        )
        val intent = Intent(context, FileDetailsActivity::class.java).apply {
            putExtras(bundle)
        }
        context.startActivity(intent)
    }

    fun isEmailValid(email: Editable?, editText: TextInputEditText): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches()
                && email != null
    }

    fun isPasswordValid(password: Editable?): Boolean {
        return password != null && password.length >= 8
    }

    fun collectionPath(level: Int?): String {
        return when (level) {
            100 -> Constants.FIRST_YEAR
            200 -> Constants.SECOND_YEAR
            300 -> Constants.THIRD_YEAR
            else -> "Not found"
        }
    }

    fun similarPapers(
        context: Context,
        recyclerView: RecyclerView,
        progressIndicator: LinearProgressIndicator,
        firestore: FirebaseFirestore,
        level: String,
        queryString: String
    ) {
        progressIndicator.visibility = View.VISIBLE
        val courses = mutableListOf<Course>()
        firestore.collection(QUESTIONS)
            .document(COURSES)
            .collection(level)
            .whereEqualTo(LECTURER, queryString)
            .get()
            .addOnSuccessListener {
                progressIndicator.visibility = View.GONE
                for (document in it) {
                    val course = Course(
                        document.getString(LECTURER),
                        document.getString(TITLE),
                        document.getString(LEVEL),
                        document.getString(YEAR),
                        document.getString(SEMESTER),
                        document.getString(QUESTION_URL),
                        document.getString(SOLUTION_URL),
                    )
                    courses.add(course)
                    recyclerView.apply {
                        adapter =
                            CourseRecyclerAdapter(CourseRecyclerAdapter.OnClickListener { course ->
                                courseBundle(context, course)
                            }, courses)
                        layoutManager = LinearLayoutManager(
                            context,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
            }
    }

}