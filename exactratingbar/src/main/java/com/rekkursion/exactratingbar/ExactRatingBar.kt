package com.rekkursion.exactratingbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View

class ExactRatingBar(context: Context, attrs: AttributeSet?): View(context, attrs) {
    // the number of stars
    private var mNumOfStars = 5
    var numOfStars get() = mNumOfStars; set(value) { mNumOfStars = value }

    // the current value of stars
    private var mValue = mNumOfStars.toFloat()
    var currentValue get() = mValue; set(value) { mValue = value }

    // the gravity
    private var mGravity = Gravity.CENTER
    var gravity get() = mGravity; set(value) { mGravity = value }

    // the spacing of each star
    private var mSpacing = 0F
    var spacing get() = mSpacing; set(value) { mSpacing = value }

    // the index of the style of each star
    private var mStarStyleIndex = 0
    var starStyle: StarStyle get() = StarStyle.values()[mStarStyleIndex]; set(value) { mStarStyleIndex = value.ordinal }

    // the rendering size of each star
    private var mStarSize = 0F
    var starSize get() = mStarSize; set(value) { mStarSize = value }

    // the color of each valued star
    private var mStarValueColor = Color.RED
    var starValueColor get() = mStarValueColor; set(value) { mStarValueColor = value }

    // the color of each star's base
    private var mStarBaseColor = Color.DKGRAY
    var starBaseColor get() = mStarBaseColor; set(value) { mStarBaseColor = value }

    // the background color
    private var mBgColor = Color.TRANSPARENT
    var bgColor get() = mBgColor; set(value) { mBgColor = value }

    // the rendering-size of each star, including the spacing
    private var mStarSizeIncludesSpacing = 0F

    // the paint for rendering
    private val mPaint = Paint()

    /* ============================================================ */

    // primary constructor
    init {
        // initialize the attributes
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ExactRatingBar)

            mNumOfStars = ta.getInteger(R.styleable.ExactRatingBar_stars_num, 5)
            mValue = ta.getFloat(R.styleable.ExactRatingBar_stars_value, 5F)
            mGravity = ta.getInt(R.styleable.ExactRatingBar_android_gravity, Gravity.CENTER)
            mSpacing = ta.getFloat(R.styleable.ExactRatingBar_spacing, 10F)
            mStarStyleIndex = ta.getInt(R.styleable.ExactRatingBar_star_style, 0)
            mStarSize = ta.getFloat(R.styleable.ExactRatingBar_star_size, 100F)
            mStarValueColor = ta.getColor(R.styleable.ExactRatingBar_star_value_color, Color.BLACK)
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
            MeasureSpec.AT_MOST -> starSize.toInt()
            // unspecified
            else -> suggestedMinimumHeight + paddingTop + paddingBottom
        }

        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val desiredWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            // match_parent or exact value given
            MeasureSpec.EXACTLY -> specWidth
            // wrap_content
            MeasureSpec.AT_MOST -> starSize.toInt() * mNumOfStars
            // unspecified
            else -> suggestedMinimumWidth + paddingLeft + paddingRight
        }
        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    // specify the layout
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mStarSizeIncludesSpacing =
            if (width < height || height.toFloat() * mNumOfStars >= width)
                width / mNumOfStars.toFloat()
            else
                height.toFloat()
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
        if (starSizeWithoutSpacing > 0F)
            for (idx in 0 until mNumOfStars) {
                val valuedRatio = if (mValue > idx + 1F) 1F else if (mValue > idx) mValue - idx else 0F
                starStyle.renderSingleStar(canvas, mPaint, mStarSizeIncludesSpacing * idx + mSpacing, mSpacing, starSizeWithoutSpacing, mStarValueColor, mStarBaseColor, valuedRatio)
            }
    }
}