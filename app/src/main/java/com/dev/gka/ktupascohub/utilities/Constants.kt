package com.dev.gka.ktupascohub.utilities

object Constants {
    // Google sign in code
    const val RC_SIGN_IN = 123

    // Pref Keys
    const val IS_FIRST_TIME = "firstrun"
    const val IS_REP = "rep"
    const val IS_STUDENT = "normalstudent"

    // Firebase Paths
    const val STORAGE_PATH = "pastquestions"
    const val COURSES = "courses"
    const val USERS = "users"
    const val REPS = "reps"

    // Broadcast Event
    const val LOG_OUT = "event_logout"

    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAdIzhaXU:APA91bGf2_f8WyUzpua3VPpkbB7RGiJNrmsCYHNpXiPdW58taXF-x80OPhSG8-EitKENvx1_f0JA9a-Dt0YlbDiNcHyc-zmpwD5LpmwYQIb7bgOc2TvwnFA8MELQHYbOHU7Y0vWhStVe"
    const val CONTENT_TYPE = "application/json"

    // FirebaseMessaging Topic
    const val TOPIC = "/topics/gkaTopics"
}