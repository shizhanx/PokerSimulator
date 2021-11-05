package com.example.pokersimulator

import android.net.Uri

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokersimulator.domain_object.PlayerData

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

    fun resetConnections() {
        connections.value = mutableMapOf()
    }
}