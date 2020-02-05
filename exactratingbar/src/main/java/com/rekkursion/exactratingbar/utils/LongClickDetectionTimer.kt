package com.rekkursion.exactratingbar.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View.OnLongClickListener
import java.util.Timer
import java.util.TimerTask
import com.rekkursion.exactratingbar.ExactRatingBar

internal class LongClickDetectionTimer(exactRatingBar: ExactRatingBar, onLongClickListener: OnLongClickListener): Timer() {
    // the place that really do the designated task which is invoked by the timer-task
    private val mHandler = @SuppressLint("HandlerLeak") object: Handler() {
        override fun handleMessage(msg: Message) {
            onLongClickListener.onLongClick(exactRatingBar)
        }
    }

    // the timer-task for sending the message to the handler
    private val mTimerTask = object: TimerTask() {
        override fun run() {
            mHandler.sendEmptyMessage(1)
        }
    }

    /* ============================================================ */

    // stop the timer
    fun stop(): LongClickDetectionTimer? {
        cancel()
        purge()
        return null
    }

    // do the designated task
    fun scheduleDesignatedTask() {
        schedule(mTimerTask, 500L)
    }
}