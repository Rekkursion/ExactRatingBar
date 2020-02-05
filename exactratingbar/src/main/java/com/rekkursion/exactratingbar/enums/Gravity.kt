package com.rekkursion.exactratingbar.enums

enum class Gravity(val flag: Int, val getPosition: (viewWidth: Float, viewHeight: Float, numOfStars: Int, starSize: Float) -> Float) {
    TOP(1, { _, _, _, _ -> 0F }),

    CENTER_VERTICAL(2, { _, viewHeight, _, starSize -> (viewHeight - starSize) / 2F }),

    BOTTOM(4, { _, viewHeight, _, starSize -> viewHeight - starSize }),

    LEFT(8, { _, _, _, _ -> 0F }),

    CENTER_HORIZONTAL(16, { viewWidth, _, numOfStars, starSize -> (viewWidth - numOfStars * starSize) / 2F }),

    RIGHT(32, { viewWidth, _, numOfStars, starSize -> viewWidth - numOfStars * starSize })
}