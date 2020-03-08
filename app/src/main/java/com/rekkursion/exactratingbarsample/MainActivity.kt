package com.rekkursion.exactratingbarsample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.rekkursion.exactratingbar.ExactRatingBar
import com.rekkursion.exactratingbar.enums.ValueChangeScale
import com.rekkursion.exactratingbar.utils.OnValueChangeListener

class MainActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var mErb: ExactRatingBar

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the text-view to displaying the values
        val mTxtvShowValues = findViewById<TextView>(R.id.txtv_show_values)

        // get the text-view to displaying the max & min values
        val mTxtvShowMaxMinValues = findViewById<TextView>(R.id.txtv_show_max_min_values)

        // get the exact-rating-bar for demo
        mErb = findViewById(R.id.erb_example)

        // set the on-value-change-listener of the erb
        mErb.setOnValueChangeListener(object: OnValueChangeListener {
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

        // set the click events on buttons to change the value scaling
        findViewById<Button>(R.id.btn_value_scale_continuous).setOnClickListener(this)
        findViewById<Button>(R.id.btn_value_scale_half_star).setOnClickListener(this)
        findViewById<Button>(R.id.btn_value_scale_single_star).setOnClickListener(this)

        // set the text of max & min values (the range of possible value)
        mTxtvShowMaxMinValues.text = "The possible value range is [${mErb.minStarsValue}, ${mErb.maxStarsValue}]"
    }

    override fun onClick(p0: View?) {
        mErb.valueChangeScale = when (p0?.id) {
            R.id.btn_value_scale_continuous -> ValueChangeScale.CONTINUOUS
            R.id.btn_value_scale_half_star -> ValueChangeScale.HALF_STAR
            R.id.btn_value_scale_single_star -> ValueChangeScale.WHOLE_SINGLE_STAR
            else -> mErb.valueChangeScale
        }
    }
}
