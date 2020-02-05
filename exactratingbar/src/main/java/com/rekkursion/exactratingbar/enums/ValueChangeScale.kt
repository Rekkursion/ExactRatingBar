package com.rekkursion.exactratingbar.enums

import android.util.Log
import com.rekkursion.exactratingbar.ExactRatingBar
import kotlin.math.max
import kotlin.math.min

enum class ValueChangeScale(val getRatingValueByViewX: (x: Float, leftMostOfStars: Float, erb: ExactRatingBar) -> Float) {
    HALF_STAR({ x, leftMostOfStars, erb ->
        val totalWidth = erb.starSize * erb.numOfStars
        when {
            x <= leftMostOfStars ->
                max(0F, erb.minStarsValue)

            x >= leftMostOfStars + totalWidth ->
                min(erb.numOfStars.toFloat(), erb.maxStarsValue)

            else -> {
                var curW = leftMostOfStars + erb.spacing
                var value = 0F
                for (starValue in 1..(erb.numOfStars)) {
                    if (x >= curW) value += 0.5F
                    curW += (erb.starSize - erb.spacing * 2F) / 2F
                    if (x >= curW) value += 0.5F
                    curW += (erb.starSize - erb.spacing * 2F) / 2F
                    curW += erb.spacing * 2F
                }

                // bound the value
                if (value < erb.minStarsValue) value = erb.minStarsValue
                if (value > erb.maxStarsValue) value = erb.maxStarsValue

                value
            }
        }
    }),

    WHOLE_SINGLE_STAR({ x, leftMostOfStars, erb ->
        val totalWidth = erb.starSize * erb.numOfStars
        when {
            x <= leftMostOfStars ->
                max(0F, erb.minStarsValue)

            x >= leftMostOfStars + totalWidth ->
                min(erb.numOfStars.toFloat(), erb.maxStarsValue)

            else -> {
                var curW = leftMostOfStars + erb.spacing
                var value = 0F
                for (starValue in 1..(erb.numOfStars)) {
                    if (x >= curW) value += 1F
                    curW += erb.starSize
                }

                // bound the value
                if (value < erb.minStarsValue) value = erb.minStarsValue
                if (value > erb.maxStarsValue) value = erb.maxStarsValue

                value
            }
        }
    }),

    CONTINUOUS({ x, leftMostOfStars, erb ->
        Log.e("f", "cc")
        val totalWidth = erb.starSize * erb.numOfStars
        when {
            x <= leftMostOfStars ->
                max(0F, erb.minStarsValue)

            x >= leftMostOfStars + totalWidth ->
                min(erb.numOfStars.toFloat(), erb.maxStarsValue)

            else -> {
                var curW = leftMostOfStars + erb.spacing
                var value = 0F

                for (starValue in 1..(erb.numOfStars)) {
                    if (x >= curW + erb.starSize - erb.spacing * 2F)
                        value += 1F
                    else if (x >= curW)
                        value += (x - curW) / (erb.starSize - erb.spacing * 2F)
                    curW += erb.starSize
                }

                // bound the value
                if (value < erb.minStarsValue) value = erb.minStarsValue
                if (value > erb.maxStarsValue) value = erb.maxStarsValue

                value
            }
        }
    })
}