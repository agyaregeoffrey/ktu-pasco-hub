package com.dev.gka.ktupascohub.utilities


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.adapters.CourseRecyclerAdapter
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Constants.COURSES
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
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
            null,
            "First",
            null
        ),
        Course("Sefakor Awurama Adabunu", "CSD 307 Operations Research 1", "300", null, "First", null),
        Course("Martin Offei", "CSD 313 Server Concepts", "300", null, "First", null),
        Course("Bright Anibreka", "CSD 317 System Administration", null, "300", "First", null),
        Course(
            "Benjamin Kwoffie",
            "CSD 319 IT & the Contemporary Manager (Entrepreneurship)",
            "300",
            null,
            "First",
            null
        )
    )

    fun firestoreData(
        context: Context,
        recyclerView: RecyclerView,
        imageView: ImageView,
        progressIndicator: LinearProgressIndicator,
        firestore: FirebaseFirestore
    ): MutableList<Course> {
        val courses = mutableListOf<Course>()
        progressIndicator.visibility = View.VISIBLE
        firestore.collection(COURSES)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressIndicator.visibility = View.GONE
                    for (documentSnapshot in it.result) {
                        val course = Course(
                            documentSnapshot.getString("lecturer"),
                            documentSnapshot.getString("title"),
                            documentSnapshot.getString("level"),
                            documentSnapshot.getString("year"),
                            documentSnapshot.getString("semester"),
                            null
                        )
                        course.file = documentSnapshot.getString("file")
                        courses.add(course)
                    }
                    if (courses.isEmpty()) {
                        imageView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        imageView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.apply {
                            adapter =
                                CourseRecyclerAdapter(CourseRecyclerAdapter.OnClickListener { course ->
                                    requestStoragePermission(context, object : PermissionsCallback {
                                        @RequiresApi(Build.VERSION_CODES.M)
                                        override fun onPermissionRequest(granted: Boolean) {
                                            if (granted) {
                                                if (hasNetworkConnected(context)) {
                                                    showSnack(
                                                        recyclerView,
                                                        "Downloading ${course.title}"
                                                    )
                                                    downloadFile(
                                                        context,
                                                        course.title!!,
                                                        course.file!!
                                                    )
                                                } else showSnack(
                                                    recyclerView,
                                                    "An active internet connection is required to download file"
                                                )
                                            } else
                                                showSnack(
                                                    recyclerView,
                                                    "Storage permission is required to download file."
                                                )
                                        }

                                    })
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


    private fun requestStoragePermission(context: Context, callback: PermissionsCallback) {
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

}