package com.dev.gka.ktupascohub.utilities

import android.view.View
import com.google.android.material.snackbar.Snackbar

object Helpers {
    fun showSnack(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }
}