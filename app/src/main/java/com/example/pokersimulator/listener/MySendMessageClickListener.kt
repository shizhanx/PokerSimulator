package com.example.pokersimulator.listener

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout

/**
 * A custom onClickListener that runs the specified actions, and then clears focus to the given
 * textInputView, and then hides the input keyboard.
 * This achieves a visual effect of sending a message in those live chat apps.
 */
class MySendMessageClickListener(
    private val context: Context,
    private val textInputView: View,
    private val otherActions: () -> Unit
): View.OnClickListener {
    override fun onClick(view: View) {
        otherActions()
        // Close keyboard and clear focus so that the user can see the buttons appearing below
        textInputView.clearFocus()
        // this is for closing the keyboard after user finishes input
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}