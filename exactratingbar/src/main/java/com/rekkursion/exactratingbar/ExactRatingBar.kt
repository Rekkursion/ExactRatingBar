package com.rekkursion.exactratingbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import kotlin.math.max
import kotlin.math.min

class ExactRatingBar(context: Context, attrs: AttributeSet?): View(context, attrs) {
    // the number of stars
    private var mNumOfStars = 5
    var numOfStars get() = mNumOfStars; set(value) { mNumOfStars = value }

    // the current value of stars
    private var mValue = 5F
    var currentValue get() = mValue; set(value) { mValue = value }

    // the gravity
    private var mGravity = com.rekkursion.exactratingbar.Gravity.CENTER_HORIZONTAL.flag or com.rekkursion.exactratingbar.Gravity.CENTER_VERTICAL.flag
    var gravity get() = mGravity; set(value) { mGravity = value }

    // the spacing of each star
    private var mSpacing = 10F
    var spacing get() = mSpacing; set(value) { mSpacing = value }

    // the index of the style of each star
    private var mStarStyleIndex = 0
    var starStyle: StarStyle get() = StarStyle.values()[mStarStyleIndex]; set(value) { mStarStyleIndex = value.ordinal }

    // the rendering size of each star which includes the spacings
    private var mStarSizeIncludesSpacing = 100F
    var starSize get() = mStarSizeIncludesSpacing; set(value) { mStarSizeIncludesSpacing = value }

    // the color of each valued star
    private var mStarValueColor = Color.RED
    var starValueColor get() = mStarValueColor; set(value) { mStarValueColor = value }

    // the color of each star's base
    private var mStarBaseColor = Color.DKGRAY
    var starBaseColor get() = mStarBaseColor; set(value) { mStarBaseColor = value }

    // the background color
    private var mBgColor = Color.TRANSPARENT
    var bgColor get() = mBgColor; set(value) { mBgColor = value }

    // the paint for rendering
    private val mPaint = Paint()

    /* ============================================================ */

    // primary constructor
    init {
        // initialize the attributes
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ExactRatingBar)

            mNumOfStars = max(1, ta.getInteger(R.styleable.ExactRatingBar_stars_num, 5))
            mValue = ta.getFloat(R.styleable.ExactRatingBar_stars_value, 5F)
            mGravity = ta.getInt(R.styleable.ExactRatingBar_gravity, com.rekkursion.exactratingbar.Gravity.CENTER_HORIZONTAL.flag or com.rekkursion.exactratingbar.Gravity.CENTER_VERTICAL.flag)
            mSpacing = ta.getFloat(R.styleable.ExactRatingBar_spacing, 10F)
            mStarStyleIndex = min(StarStyle.values().size - 1, max(0, ta.getInt(R.styleable.ExactRatingBar_star_style, 0)))
            mStarSizeIncludesSpacing = max(0F, ta.getFloat(R.styleable.ExactRatingBar_star_size, 100F))
            mStarValueColor = ta.getColor(R.styleable.ExactRatingBar_star_value_color, Color.RED)
            mStarBaseColor = ta.getColor(R.styleable.ExactRatingBar_star_base_color, Color.DKGRAY)
            mBgColor = ta.getColor(R.styleable.ExactRatingBar_bg_color, Color.TRANSPARENT)

            ta.recycle()
        }
    }

    // secondary constructor
    constructor(context: Context): this(context, null)

    /* ============================================================ */

    // measure the sizes
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)
        val desiredHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            // match_parent or exact value given
            MeasureSpec.EXACTLY -> specHeight
            // wrap_content
            MeasureSpec.AT_MOST -> mStarSizeIncludesSpacing.toInt()
            // unspecified
            else -> suggestedMinimumHeight + paddingTop + paddingBottom
        }

        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val desiredWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            // match_parent or exact value given
            MeasureSpec.EXACTLY -> specWidth
            // wrap_content
            MeasureSpec.AT_MOST -> mStarSizeIncludesSpacing.toInt() * mNumOfStars
            // unspecified
            else -> suggestedMinimumWidth + paddingLeft + paddingRight
        }
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    // render
    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            // render the background
            mPaint.color = mBgColor
            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), mPaint)

            // render stars
            renderStars(canvas)
        }
    }

    /* ============================================================ */

    // render the stars
    private fun renderStars(canvas: Canvas) {
        // get the enumerate type of the star-style
        val starStyle = StarStyle.values()[mStarStyleIndex]

        // set the star's color
        mPaint.color = mStarValueColor

        // double the spacing's value
        val doubledSpacing = mSpacing * 2F

        // get the star's size w/o spacing
        //val starSizeWithoutSpacing = if (doubledSpacing > mStarSizeIncludesSpacing) mStarSizeIncludesSpacing else mStarSizeIncludesSpacing - doubledSpacing
        val starSizeWithoutSpacing = mStarSizeIncludesSpacing - doubledSpacing

        // render stars only if the size is bigger than zero
        if (starSizeWithoutSpacing > 0F) {
            val upLeftCornerOfFirstStar = getUpLeftCornerOfFirstStar()
            for (idx in 0 until mNumOfStars) {
                val valuedRatio = if (mValue > idx + 1F) 1F else if (mValue > idx) mValue - idx else 0F
                starStyle.renderSingleStar(
                    canvas,
                    mPaint,
                    mStarSizeIncludesSpacing * idx + mSpacing + upLeftCornerOfFirstStar.x,
                    mSpacing + upLeftCornerOfFirstStar.y,
                    starSizeWithoutSpacing,
                    mStarValueColor,
                    mStarBaseColor,
                    valuedRatio
                )
            }
        }
    }

    // get the up-left corner of the first star to position all of stars
    private fun getUpLeftCornerOfFirstStar(): PointF {
        // 1: top, 2: cen_vert, 4: bottom
        // 8: left, 16, cen_hori, 32: right
        var x = 0F; var y = 0F
        var flag = mGravity
        for (g in com.rekkursion.exactratingbar.Gravity.values().reversed()) {
            if (flag >= g.flag) {
                flag -= g.flag
                if (g.flag == 1 || g.flag == 2 || g.flag == 4)
                    y = g.getPosition(width.toFloat(), height.toFloat(), mNumOfStars, mStarSizeIncludesSpacing)
                else
                    x = g.getPosition(width.toFloat(), height.toFloat(), mNumOfStars, mStarSizeIncludesSpacing)
            }
        }

        return PointF(x, y)
    }
}