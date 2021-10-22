package com.dev.gka.ktupascohub.activities.rep

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class UploadActivityViewModel : ViewModel() {
    private val _uri = MutableLiveData<Uri>()
    val uri: LiveData<Uri>
        get() = _uri

    private val _questionSelected = MutableLiveData<Boolean>()
    val questionSelected: LiveData<Boolean>
        get() = _questionSelected

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    fun fileUri(uri: Uri) {
        _uri.value = uri
    }

    fun fileName(name: String) {
        _name.value = name
        Timber.d("${_name.value}")
    }

    fun questionSelected(b: Boolean) {
        _questionSelected.value = b
    }
}