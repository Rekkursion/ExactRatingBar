package com.rekkursion.exactratingbarsample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.rekkursion.exactratingbar.ExactRatingBar
import com.rekkursion.exactratingbar.utils.OnValueChangeListener

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the text-view to displaying the values
        val mTxtvShowValues = findViewById<TextView>(R.id.txtv_show_values)

        // get the exact-rating-bar for demo
        val mErb = findViewById<ExactRatingBar>(R.id.erb_example)

        // set the on-value-change-listener of the erb
        mErb.setOnValueChangeListener(object: OnValueChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onValueChange(oldValue: Float, newValue: Float): Boolean {
                // set the text to display the old & new values
                mTxtvShowValues.text = "old: $oldValue, new: $newValue"

                /**
                 * return true  -> really change the value of the erb
                 * return false -> not actual change of the erb
                 **/
                return true
            }
        })
    }
}
