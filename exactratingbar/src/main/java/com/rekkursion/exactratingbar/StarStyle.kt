package com.rekkursion.exactratingbar

import android.graphics.*
import kotlin.math.*

enum class StarStyle(val getShapedPath: (x: Float, y: Float, size: Float) -> Path) {
    STAR({ x, y, size ->
        val r = size / 2F
        val cX = x + r
        val cY = y + r

        // the path object
        val path = Path()
        // get the points of a star shape
        for (idx in 0..5) {
            val angle = ((idx * 144 - 90) * PI / 180).toFloat()
            path.lineTo(cX + r * cos(angle), cY + r * sin(angle))
        }
        // close the path
        path.close()
        // return the path
        path
    }),

    CIRCLE({ x, y, size ->
        // the radius (ban-jing)
        val r = size / 2F
        // the path object
        val path = Path()
        // add a circle into the path
        path.addCircle(x + r, y + r, r, Path.Direction.CW)
        // return the path
        path
    }),

    SQUARE({ x, y, size ->
        // the path object
        val path = Path()
        // add a square into the path
        path.addRect(x, y, x + size, y + size, Path.Direction.CW)
        // return the path
        path
    });

    // render a single star on the passed canvas using the passed paint
    fun renderSingleStar(canvas: Canvas, paint: Paint, x: Float, y: Float, size: Float, valueColor: Int, baseColor: Int, valuedRatio: Float) {
        // get the path whose shape is determined by each type
        val path = getShapedPath(x, y, size)

        // set to the base color
        paint.color = baseColor
        // render the path as a star
        canvas.drawPath(path, paint)

        // set to the value color
        paint.color = valueColor
        // render the value star by the mask (xfermode)
        val sc = canvas.saveLayer(x, y, x + size, y + size, null, Canvas.ALL_SAVE_FLAG)
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawRect(x + size * valuedRatio, y, x + size, y + size, paint)
        paint.xfermode = null
        canvas.restoreToCount(sc)
    }
}