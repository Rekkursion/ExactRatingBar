package com.rekkursion.exactratingbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class ExactRatingBar(context: Context, attrs: AttributeSet?): View(context, attrs) {
    // could the value be altered or not
    private var mAlterable = true
    var alterable get() = mAlterable; set(value) { mAlterable = value; invalidate() }

    // the number of stars
    private var mNumOfStars = 5
    var numOfStars get() = mNumOfStars; set(value) { mNumOfStars = value; invalidate() }

    // the current value of stars
    private var mValue = 5F
    var currentValue get() = mValue; set(value) { mValue = min(mMaxStarsValue, max(mMinStarsValue, value)); invalidate() }

    // the minimum value of stars
    private var mMinStarsValue = 0F
    var minStarsValue get() = mMinStarsValue; set(value) { mMinStarsValue = value; invalidate() }

    // the maximum value of stars
    private var mMaxStarsValue = 5F
    var maxStarsValue get() = mMaxStarsValue; set(value) { mMaxStarsValue = value; invalidate() }

    // the gravity's flag
    private var mGravityFlag = com.rekkursion.exactratingbar.Gravity.CENTER_HORIZONTAL.flag or com.rekkursion.exactratingbar.Gravity.CENTER_VERTICAL.flag

    // the spacing of each star
    private var mSpacing = 10F
    var spacing get() = mSpacing; set(value) { mSpacing = value; invalidate() }

    // the index of the style of each star
    private var mStarStyleIndex = 0
    var starStyle: StarStyle get() = StarStyle.values()[mStarStyleIndex]; set(value) { mStarStyleIndex = value.ordinal; invalidate() }

    // the rendering size of each star which includes the spacings
    private var mStarSizeIncludesSpacing = 100F
    var starSize get() = mStarSizeIncludesSpacing; set(value) { mStarSizeIncludesSpacing = value; invalidate() }

    // the color of each valued star
    private var mStarValueColor = Color.parseColor("#F88B30")
    var starValueColor get() = mStarValueColor; set(value) { mStarValueColor = value; invalidate() }

    // the color of each star's base
    private var mStarBaseColor = Color.LTGRAY
    var starBaseColor get() = mStarBaseColor; set(value) { mStarBaseColor = value; invalidate() }

    // the color of each star's base when pressing down
    private var mStarBasePressedColor = Color.parseColor("#77F4AC92")
    var starBasePressedColor get() = mStarBasePressedColor; set(value) { mStarBasePressedColor = value; invalidate() }

    // the background color
    private var mBgColor = Color.TRANSPARENT
    var bgColor get() = mBgColor; set(value) { mBgColor = value; invalidate() }

    // the paint for rendering
    private val mPaint = Paint()

    // the listener when the value has been changed
    private var mOnValueChangedListener: OnValueChangedListener? = null

    // check if the finger is touched
    private var mIsTouched = false

    // the default on-touch-listener
    private val mDefaultOnTouchListener = OnTouchListener { _, motionEvent ->
        if (alterable) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> mIsTouched = true
                MotionEvent.ACTION_UP -> mIsTouched = false
            }

            val newValue = getRatingValueByViewX(motionEvent.x)
            if (mOnValueChangedListener?.onValueChanged(mValue, newValue) == true) mValue = newValue

            invalidate()
        }
        false
    }

    // the default on-long-click-listener
    private val mDefaultOnLongClickListener = OnLongClickListener { true }

    /* ============================================================ */

    // primary constructor
    init {
        // initialize the attributes
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ExactRatingBar)

            mAlterable = ta.getBoolean(R.styleable.ExactRatingBar_alterable, true)
            mNumOfStars = max(1, ta.getInteger(R.styleable.ExactRatingBar_stars_num, 5))
            mValue = ta.getFloat(R.styleable.ExactRatingBar_stars_value, 5F)
            mMinStarsValue = ta.getFloat(R.styleable.ExactRatingBar_min_stars_value, 0F)
            mMaxStarsValue = ta.getFloat(R.styleable.ExactRatingBar_max_stars_value, 5F)
            mGravityFlag = ta.getInt(R.styleable.ExactRatingBar_gravity, com.rekkursion.exactratingbar.Gravity.CENTER_HORIZONTAL.flag or com.rekkursion.exactratingbar.Gravity.CENTER_VERTICAL.flag)
            mSpacing = ta.getFloat(R.styleable.ExactRatingBar_spacing, 10F)
            mStarStyleIndex = min(StarStyle.values().size - 1, max(0, ta.getInt(R.styleable.ExactRatingBar_star_style, 0)))
            mStarSizeIncludesSpacing = max(0F, ta.getFloat(R.styleable.ExactRatingBar_star_size, 100F))
            mStarValueColor = ta.getColor(R.styleable.ExactRatingBar_star_value_color, Color.parseColor("#F88B30"))
            mStarBaseColor = ta.getColor(R.styleable.ExactRatingBar_star_base_color, Color.LTGRAY)
            mStarBasePressedColor = ta.getColor(R.styleable.ExactRatingBar_star_base_pressed_color, Color.parseColor("#77F4AC92"))
            mBgColor = ta.getColor(R.styleable.ExactRatingBar_bg_color, Color.TRANSPARENT)

            ta.recycle()
        }

        // initialize the events
        initEvents()
    }

    // secondary constructor
    constructor(context: Context): this(context, null)

    /* ============================================================ */

    // set the listener for listening the value's change
    fun setOnValueChangedListener(onValueChangedListener: OnValueChangedListener) {
        mOnValueChangedListener = onValueChangedListener
    }

    // set the gravity by
    fun setGravity(vararg gravities: Gravity) {
        var flagValue = 0
        gravities.forEach { flagValue += it.flag }
        mGravityFlag = flagValue
    }

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

    // override the set-on-touch-listener to do the default listener
    override fun setOnTouchListener(l: OnTouchListener?) {
        val onTouchListener = OnTouchListener { p0, p1 ->
            val defaultReturnValue = mDefaultOnTouchListener.onTouch(p0, p1)
            l?.onTouch(p0, p1) ?: defaultReturnValue
        }
        super.setOnTouchListener(onTouchListener)
    }

    // override the set-on-long-click-listener to do the default listener
    override fun setOnLongClickListener(l: OnLongClickListener?) {
        val onLongClickListener = OnLongClickListener {
            val defaultReturnValue = mDefaultOnLongClickListener.onLongClick(it)
            l?.onLongClick(it) ?: defaultReturnValue
        }
        super.setOnLongClickListener(onLongClickListener)
    }

    /* ============================================================ */

    // initialize events
    private fun initEvents() {
        // the touch event
        super.setOnTouchListener(mDefaultOnTouchListener)

        // the long-click event
        super.setOnLongClickListener(mDefaultOnLongClickListener)

        // the default on-value-changed-listener
        setOnValueChangedListener(object: OnValueChangedListener { override fun onValueChanged(oldValue: Float, newValue: Float): Boolean { return true } })
    }

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
                    if (mIsTouched) mStarBasePressedColor else mStarBaseColor,
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
        var flag = mGravityFlag
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

    // get the value of the exact-rating-bar by a certain x-axis value
    private fun getRatingValueByViewX(x: Float): Float {
        val leftMostOfStars = getUpLeftCornerOfFirstStar().x
        val totalWidth = mStarSizeIncludesSpacing * mNumOfStars

        if (x <= leftMostOfStars)
            return max(0F, mMinStarsValue)
        else if (x >= leftMostOfStars + totalWidth)
            return min(mNumOfStars.toFloat(), mMaxStarsValue)

        var curW = leftMostOfStars + mSpacing
        var value = 0F
        for (starValue in 1..mNumOfStars) {
            if (x >= curW) value += 0.5F
            curW += (mStarSizeIncludesSpacing - mSpacing * 2F) / 2F
            if (x >= curW) value += 0.5F
            curW += (mStarSizeIncludesSpacing - mSpacing * 2F) / 2F
            curW += mSpacing * 2F
        }

        // bound the value
        if (value < mMinStarsValue) value = mMinStarsValue
        if (value > mMaxStarsValue) value = mMaxStarsValue

        return value
    }
}