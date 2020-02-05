package com.rekkursion.exactratingbar

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message

class LongClickDetectionTimer(exactRatingBar: ExactRatingBar): java.util.Timer() {
    // the exact-rating-bar that will be affected
    private val mExactRatingBar = exactRatingBar

    // the place that really do the designated task which is invoked by the timer-task
    private val mHandler = @SuppressLint("HandlerLeak") object: Handler() {
        override fun handleMessage(msg: Message) {

        }
    }

    // the timer-task for sending the message to the handler
    private val mTimerTask = object: java.util.TimerTask() {
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
        schedule(mTimerTask, 800L)
    }
}