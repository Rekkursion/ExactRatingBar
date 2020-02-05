package com.rekkursion.exactratingbar.utils

interface OnValueChangeListener {
    // be invoked when the value of the exact-rating-bar is about to be changed
    fun onValueChange(oldValue: Float, newValue: Float): Boolean
}