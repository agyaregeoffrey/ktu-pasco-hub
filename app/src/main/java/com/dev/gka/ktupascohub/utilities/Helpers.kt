package com.dev.gka.ktupascohub.utilities

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.adapters.CourseRecyclerAdapter
import com.dev.gka.ktupascohub.models.Course
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

object Helpers {
    fun showSnack(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    // Dummy data
    val papers = listOf(
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Solomon Anab", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Solomon Anab", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
        Course("Samuel Tawiah", "Data Structures & Algorithm", "2020", "Semester 2", null),
    )

    val courses = listOf(
        Course("CSD 233 Social, Legal & Ethical Issues in Computing", "Florence Agyeiwaa", "300", "First", null),
        Course("CSD 307 Operations Research I", "Sefakor Awurama Adabunu", "300", "First", null),
        Course("CSD 313 Server Concepts", "Martin Offei", "300", "First", null),
        Course("CSD 317 System Administration", "Bright Anibreka", "300", "First", null),
        Course("CSD 319 IT & the Contemporary Manager (Entrepreneurship)", "Benjamin Kwoffie", "300", "First", null)
    )

    fun firestoreData(recyclerView: RecyclerView, progressIndicator: LinearProgressIndicator, firestore: FirebaseFirestore) {
        val courses = mutableListOf<Course>()
        progressIndicator.visibility = View.VISIBLE
        firestore.collection(Constants.COURSES)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    progressIndicator.visibility = View.GONE
                    for (documentSnapshot in it.result) {
                        val course = Course(documentSnapshot.getString("lecturer"),
                            documentSnapshot.getString("title"),
                            documentSnapshot.getString("level"),
                            documentSnapshot.getString("semester"), null)
                        course.file = documentSnapshot.getString("file")
                        courses.add(course)
                    }
                    recyclerView.apply {
                        adapter = CourseRecyclerAdapter(CourseRecyclerAdapter.OnClickListener {

                        }, courses)
                        layoutManager = LinearLayoutManager(
                            context,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
            }.addOnFailureListener {
                Timber.e("Data load failed: ${it.message}")
            }
    }

}