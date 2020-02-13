package com.rekkursion.exactratingbarsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.rekkursion.exactratingbar.ExactRatingBar
import com.rekkursion.exactratingbar.enums.ValueChangeScale

class MainActivity: AppCompatActivity() {
    private lateinit var mFlyContentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFlyContentContainer = findViewById(R.id.fly_field_content_container)

        val erb = ExactRatingBar(this)
        erb.valueChangeScale = ValueChangeScale.HALF_STAR
        erb.isIndicator = false

        mFlyContentContainer.removeAllViews()
        mFlyContentContainer.addView(erb)
    }
}
