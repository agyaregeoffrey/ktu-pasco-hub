package com.dev.gka.ktupascohub.models

import android.os.Parcelable
import android.view.View
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    val lecturer: String?,
    val title: String?,
    val level: String?,
    val year: String?,
    val semester: String?,
    var question: String?,
    var solution: String?,
) : Parcelable