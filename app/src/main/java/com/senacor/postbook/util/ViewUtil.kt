package com.senacor.postbook.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun Activity.hideKeyboard() {
    currentFocus?.let { focusedView ->
        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }
}

fun View.hideKeyboard() {
    context.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(this.windowToken, 0)
}
