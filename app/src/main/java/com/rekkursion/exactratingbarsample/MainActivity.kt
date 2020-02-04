package com.rekkursion.exactratingbarsample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rekkursion.exactratingbar.ExactRatingBar
import com.rekkursion.exactratingbar.Gravity
import com.rekkursion.exactratingbar.OnValueChangedListener
import com.rekkursion.exactratingbar.StarStyle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
