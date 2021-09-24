package com.example.pokersimulator.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyYesNoDialog(
    private val message: String,
    private val yesOption: String,
    private val noOption: String,
    private val yesAction: () -> Unit,
    private val noAction: () -> Unit,
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setPositiveButton(yesOption) { _, _ ->
                    yesAction()
                }
                .setNegativeButton(noOption) { _, _ ->
                    noAction()
                }
            builder.create()
        }
    }
}