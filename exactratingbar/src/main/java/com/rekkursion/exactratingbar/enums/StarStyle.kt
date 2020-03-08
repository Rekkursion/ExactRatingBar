package com.rekkursion.exactratingbar.enums

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
    }),

    TRIANGLE({ x, y, size ->
        val r = size / 2F
        val cX = x + r
        val cY = y + r
        // the offset of y-axis
        val offsetY = 0.25F * r

        // the path object
        val path = Path()
        // move to the top point
        path.moveTo(cX, y + offsetY)
        // add three lines that comprise a regular triangle
        for (idx in 0..2) {
            val angle = ((idx * 120 - 90) * PI / 180).toFloat()
            path.lineTo(cX + r * cos(angle), cY + r * sin(angle) + offsetY)
        }
        path.close()
        // return the path
        path
    }),

    RHOMBUS({ x, y, size ->
        val r = size / 2F
        val cX = x + r
        val cY = y + r

        // the path object
        val path = Path()
        // move to the top point
        path.moveTo(cX, y)
        // add four lines as a rhombus
        path.lineTo(x + size, cY)
        path.lineTo(cX, y + size)
        path.lineTo(x, cY)
        path.lineTo(cX, y)
        path.close()
        // return the path
        path
    }),

    DIAMOND({ x, y, size ->
        val firstHorizontalLineWidth = size * 0.614F
        @Suppress("UnnecessaryVariable")
        val secondHorizontalLineWidth = size
        val heightBetweenTwoHorizontalLines = size * 0.27333F
        val totalHeight = size * 0.86F

        // the path object
        val path = Path()
        // move to the start point
        path.moveTo(x + (secondHorizontalLineWidth - firstHorizontalLineWidth) / 2F, y + (size - totalHeight) / 2F)
        // add lines to construct a shape of diamond
        path.rLineTo(firstHorizontalLineWidth, 0F)
        path.rLineTo((secondHorizontalLineWidth - firstHorizontalLineWidth) / 2F, heightBetweenTwoHorizontalLines)
        path.rLineTo(-(secondHorizontalLineWidth / 2F), totalHeight - heightBetweenTwoHorizontalLines)
        path.rLineTo(-(secondHorizontalLineWidth / 2F), -(totalHeight - heightBetweenTwoHorizontalLines))
        path.rLineTo((secondHorizontalLineWidth - firstHorizontalLineWidth) / 2F, -heightBetweenTwoHorizontalLines)
        path.close()
        // return the path
        path
    }),

    HEXAGRAM({ x, y, size ->
        val bigR = size / 2F
        val smallR = bigR / sqrt(3F)
        val cX = x + bigR
        val cY = y + bigR

        // the path object
        val path = Path()
        // move to top & horizontal-center
        path.moveTo(cX, y)
        // build up a hexagram
        for (idx in 0..5) {
            var angle = ((idx * 60 + 30 - 90) * PI / 180).toFloat()
            path.lineTo(cX + smallR * cos(angle), cY + smallR * sin(angle))
            angle = ((idx * 60 + 60 - 90) * PI / 180).toFloat()
            path.lineTo(cX + bigR * cos(angle), cY + bigR * sin(angle))
        }
        path.close()
        // return the path
        path
    }),

    LIGHT({ x, y, size ->
        val bigR = size / 2F
        val smallR = bigR / 6.77F
        val cX = x + bigR
        val cY = y + bigR

        // the path object
        val path = Path()
        // move to top & horizontal-center
        path.moveTo(cX, y)
        // build up a hexagram
        for (idx in 0..3) {
            var angle = ((idx * 90 + 45 - 90) * PI / 180).toFloat()
            path.lineTo(cX + smallR * cos(angle), cY + smallR * sin(angle))
            angle = ((idx * 90 + 90 - 90) * PI / 180).toFloat()
            path.lineTo(cX + bigR * cos(angle), cY + bigR * sin(angle))
        }
        path.close()
        // return the path
        path
    });

    /* ============================================================ */

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