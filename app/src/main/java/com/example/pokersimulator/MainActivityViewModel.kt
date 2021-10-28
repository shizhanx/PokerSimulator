package com.example.pokersimulator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * This viewModel is owned by the main activity, therefore available for ALL the fragments, as a
 * single source of truth.
 * The connections may update at any time,
 */
class MainActivityViewModel: ViewModel() {
    val connections: LiveData<MutableMap<Int, String>> = MutableLiveData(mutableMapOf())
    val isHost = false
    var username = ""
}