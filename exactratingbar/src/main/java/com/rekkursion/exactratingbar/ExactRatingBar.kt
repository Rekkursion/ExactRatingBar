package com.rekkursion.exactratingbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.rekkursion.exactratingbar.enums.Gravity
import com.rekkursion.exactratingbar.enums.StarStyle
import com.rekkursion.exactratingbar.enums.ValueChangeScale
import com.rekkursion.exactratingbar.utils.LongClickDetectionTimer
import com.rekkursion.exactratingbar.utils.OnValueChangeListener
import kotlin.math.max
import kotlin.math.min

class ExactRatingBar(context: Context, attrs: AttributeSet?): View(context, attrs) {
    // could the value be altered or not
    private var mIsIndicator = false
    var isIndicator get() = mIsIndicator; set(value) { mIsIndicator = value; invalidate() }

    // the number of stars
    private var mNumOfStars = 5
    var numOfStars get() = mNumOfStars; set(value) { mNumOfStars = value; invalidate() }

    // the scale of the value's change
    private var mValueChangeScaleIndex = 0
    var valueChangeScale: ValueChangeScale get() = ValueChangeScale.values()[mValueChangeScaleIndex]; set(value) { mValueChangeScaleIndex = value.ordinal; invalidate() }

    // the current value of stars
    private var mValue = mNumOfStars.toFloat()
    var currentValue get() = mValue; set(value) { mValue = min(mMaxStarsValue, max(mMinStarsValue, value)); invalidate() }

    // the minimum value of stars
    private var mMinStarsValue = 0F
    var minStarsValue get() = mMinStarsValue; set(value) { mMinStarsValue = value; invalidate() }

    // the maximum value of stars
    private var mMaxStarsValue = mNumOfStars.toFloat()
    var maxStarsValue get() = mMaxStarsValue; set(value) { mMaxStarsValue = value; invalidate() }

    // the gravity's flag
    private var mGravityFlag = Gravity.CENTER_HORIZONTAL.flag or Gravity.CENTER_VERTICAL.flag

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
    private var mOnValueChangeListener: OnValueChangeListener? = null

    // the listener when the long-click event is invoked
    private var mOnLongClickListener: OnLongClickListener? = null

    // check if the finger is touched
    private var mIsTouched = false

    // the timer used to detect the long-click event
    private var mLongClickDetectionTimer: LongClickDetectionTimer? = null

    // the default on-touch-listener
    private val mDefaultOnTouchListener = OnTouchListener { _, motionEvent ->
        if (!mIsIndicator) {
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                mIsTouched = true
                if (mOnLongClickListener != null) {
                    mLongClickDetectionTimer = mLongClickDetectionTimer?.stop()
                    mLongClickDetectionTimer =
                        LongClickDetectionTimer(this, mOnLongClickListener!!)
                    mLongClickDetectionTimer?.scheduleDesignatedTask()
                }
            }
            else {
                if (motionEvent.action == MotionEvent.ACTION_UP)
                    mIsTouched = false
                if (mOnLongClickListener != null)
                    mLongClickDetectionTimer = mLongClickDetectionTimer?.stop()
            }

            val newValue = getRatingValueByViewX(motionEvent.x)
            if (mValue != newValue && mOnValueChangeListener?.onValueChange(mValue, newValue) == true)
                mValue = newValue

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

            mIsIndicator = ta.getBoolean(R.styleable.ExactRatingBar_is_indicator, false)
            mNumOfStars = max(1, ta.getInteger(R.styleable.ExactRatingBar_stars_num, 5))
            mValueChangeScaleIndex = min(ValueChangeScale.values().size - 1, max(0, ta.getInt(R.styleable.ExactRatingBar_value_change_scale, 0)))
            mValue = ta.getFloat(R.styleable.ExactRatingBar_stars_value, mNumOfStars.toFloat())
            mMinStarsValue = ta.getFloat(R.styleable.ExactRatingBar_min_stars_value, 0F)
            mMaxStarsValue = ta.getFloat(R.styleable.ExactRatingBar_max_stars_value, mNumOfStars.toFloat())
            mGravityFlag = ta.getInt(R.styleable.ExactRatingBar_gravity, Gravity.CENTER_HORIZONTAL.flag or Gravity.CENTER_VERTICAL.flag)
            mSpacing = ta.getDimensionPixelSize(R.styleable.ExactRatingBar_spacing, 10).toFloat()
            mStarStyleIndex = min(StarStyle.values().size - 1, max(0, ta.getInt(R.styleable.ExactRatingBar_star_style, 0)))
            mStarSizeIncludesSpacing = max(0, ta.getDimensionPixelSize(R.styleable.ExactRatingBar_star_size, 100)).toFloat()
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
    fun setOnValueChangeListener(onValueChangeListener: OnValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener
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

        if (desiredWidth == 0 || desiredHeight == 0)
            setMeasuredDimension(
                mStarSizeIncludesSpacing.toInt() * mNumOfStars,
                mStarSizeIncludesSpacing.toInt()
            )
        else
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
        mOnLongClickListener = l
    }

    /* ============================================================ */

    // initialize events
    private fun initEvents() {
        // the touch event
        super.setOnTouchListener(mDefaultOnTouchListener)

        // the long-click event
        super.setOnLongClickListener(mDefaultOnLongClickListener)

        // the default on-value-changed-listener
        setOnValueChangeListener(object: OnValueChangeListener { override fun onValueChange(oldValue: Float, newValue: Float): Boolean { return true } })
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
        for (g in Gravity.values().reversed()) {
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
        return ValueChangeScale.values()[mValueChangeScaleIndex].getRatingValueByViewX(x, leftMostOfStars, this)
    }
}
