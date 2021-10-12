package com.dev.gka.ktupascohub.utilities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.dev.gka.ktupascohub.models.Student
import com.dev.gka.ktupascohub.utilities.Constants.IS_FIRST_TIME
import com.dev.gka.ktupascohub.utilities.Constants.IS_REP
import com.dev.gka.ktupascohub.utilities.Constants.IS_STUDENT
import com.dev.gka.ktupascohub.utilities.Constants.STUDENT_LEVEL

class PrefManager private constructor() {

    fun saveUserInfo(student: Student) {
        editor!!.putString("name", student.name)
        editor!!.putString("imageUrl", student.imageUrl.toString())
        editor!!.putString("email", student.email)
        editor!!.putString("phone", student.phone)
        editor!!.apply()
    }

    fun studentLevel(level: String) {
        editor!!.putString(STUDENT_LEVEL, level)
        editor!!.apply()
    }

    fun getStudentLevel(): String? {
        return sharedPreferences!!.getString(STUDENT_LEVEL, null)
    }

    fun accountSelectionOpened(b: Boolean) {
        editor!!.putBoolean(IS_FIRST_TIME, b)
        editor!!.apply()
    }

    fun repSignUpStatus(b: Boolean) {
        editor!!.putBoolean(IS_REP, b)
        editor!!.apply()
    }

    fun studentSignInStatus(b: Boolean) {
        editor!!.putBoolean(IS_STUDENT, b)
        editor!!.apply()
    }

    fun getDisplayName(): String? {
        return sharedPreferences!!.getString("name", null)
    }

    fun getImageUrl(): String? {
        return sharedPreferences!!.getString("imageUrl", "")
    }

    fun getEmail(): String? {
        return sharedPreferences!!.getString("email", "")
    }

    fun getUserPhone(): String? {
        return sharedPreferences!!.getString("phone", "No Record Found")
    }

    fun getLevel(): String? {
        return sharedPreferences!!.getString(STUDENT_LEVEL, "100")
    }

    fun hasAccountSelectionRun(): Boolean {
        return sharedPreferences!!.getBoolean(IS_FIRST_TIME, false)
    }

    fun hasRepSignedIn(): Boolean {
        return sharedPreferences!!.getBoolean(IS_REP, false)
    }

    fun hasStudentSignedIn(): Boolean {
        return sharedPreferences!!.getBoolean(IS_STUDENT, false)
    }


    companion object {
        private val prefManager = PrefManager()
        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null

        @Synchronized
        fun getInstance(context: Context?): PrefManager {
            if (sharedPreferences == null) {
                sharedPreferences = context?.getSharedPreferences(
                    context.packageName,
                    Activity.MODE_PRIVATE
                )
                editor = sharedPreferences!!.edit()
            }
            return prefManager
        }
    }
}