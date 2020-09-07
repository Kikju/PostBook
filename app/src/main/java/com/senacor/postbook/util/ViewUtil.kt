package com.senacor.postbook.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.getSystemService
import androidx.core.view.children
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun Activity.hideKeyboard() {
    currentFocus?.let { focusedView ->
        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }
}

fun View.hideKeyboard() {
    context.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun TextInputLayout.showErrorIcon(errorMessage: String? = null) {
    error = errorMessage ?: " "
    children.last().takeIf { it is LinearLayout && it.children.none { it is TextInputEditText } }?.visibility = View.GONE
}