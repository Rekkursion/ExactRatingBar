package com.rekkursion.exactratingbar.utils

interface OnValueChangedListener {
    // be invoked when the value of the exact-rating-bar has been changed
    fun onValueChanged(oldValue: Float, newValue: Float): Boolean
}