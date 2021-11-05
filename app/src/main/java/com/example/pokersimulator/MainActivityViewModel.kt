package com.example.pokersimulator

import android.net.Uri

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * This viewModel is owned by the main activity, therefore available for ALL the fragments, as a
 * single source of truth.
 * The connections may update at any time,
 */
class MainActivityViewModel: ViewModel() {
    private val connections: MutableLiveData<MutableMap<Int, String>> = MutableLiveData(mutableMapOf())
    var isHost = false
    var username = ""
    var roomPath = ""
    var imageURI = MutableLiveData<Uri?>(null)
    val isCoverImageShowing = MutableLiveData(false)
    val database = Firebase.database("https://mystical-binder-330900-default-rtdb.asia-southeast1.firebasedatabase.app/")

    fun resetConnections() {
        connections.value = mutableMapOf()
    }
}