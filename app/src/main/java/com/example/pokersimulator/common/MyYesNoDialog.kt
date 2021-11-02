package com.example.pokersimulator.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

/**
 * A yes or no dialog that executes some action after the user clicks on the options.
 */
class MyYesNoDialog(
    private val message: String,
    private val yesOption: String,
    private val noOption: String,
    private val yesAction: () -> Unit,
    private val noAction: () -> Unit,
    private val dismissAction: () -> Unit,
) : DialogFragment() {

    /**
     * Code template referred from Android Developer official document
     */
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissAction()
    }
}