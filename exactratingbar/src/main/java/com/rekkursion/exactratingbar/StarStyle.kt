package com.rekkursion.exactratingbar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.*

enum class StarStyle(val singleStarRenderFunc: (Canvas, Paint, Float, Float, Float, Int, Int, Float) -> Unit) {
    STAR({ canvas, paint, x, y, size, valueColor, baseColor, valuedRatio ->
        // ban-jing
        val r = size / 2F
        // the coordinate of the center
        val cX = x + r; val cY = y + r

        // the path object for rendering the complex polygon
        val path = Path()
        // get the points of a star shape
        for (idx in 0..5) {
            val angle = ((idx * 144 - 90) * PI / 180).toFloat()
            path.lineTo(cX + r * cos(angle), cY + r * sin(angle))
        }
        // close the path
        path.close()

        // render the path
        paint.color = baseColor
        canvas.drawPath(path, paint)
    }),

    CIRCLE({ canvas, paint, x, y, size, valueColor, baseColor, valuedRatio ->
        // set to the base color
        paint.color = baseColor
        // render a base circle
        canvas.drawOval(x, y, x + size, y + size, paint)

        if (valuedRatio > 0F) {
            val valuedW = x + size * valuedRatio
            val r = size / 2F
            val cX = x + r
            val theta = acos((valuedW - cX) / r)

            val path = Path()
            path.addArc(x,
                y,
                x + size,
                y + size,
                theta * 180F / PI.toFloat(),
                360F - 2F * (theta * 180F / PI.toFloat())
            )
            // close the path
            path.close()

            // set to the value color
            paint.color = valueColor
            // render a value circle
            canvas.drawPath(path, paint)
        }
    }),

    SQUARE({ canvas, paint, x, y, size, valueColor, baseColor, valuedRatio ->
        // set to the base color
        paint.color = baseColor
        // render a base square
        canvas.drawRect(x, y, x + size, y + size, paint)

        if (valuedRatio > 0F) {
            // set to the value color
            paint.color = valueColor
            // render a value square
            canvas.drawRect(x, y, x + size * valuedRatio, y + size, paint)
        }
    })
}