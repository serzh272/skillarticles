package ru.skillbranch.skillarticles.extensions

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.run {
        leftMargin = left
        rightMargin = right
        topMargin = top
        bottomMargin = bottom
    }
    requestLayout()
}

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.screenHeight(): Int {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.heightPixels - (24 * displayMetrics.density).toInt() //24dp status bar height
}