package com.github.windsekirun.yukarisynthesizer.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.github.windsekirun.yukarisynthesizer.R
import java.util.*

/**
 * Created by fyu on 11/3/14.
 * Original comes from https://github.com/skyfishjy/android-ripple-background
 * @FIXME 2018-12-13 WindSekirun
 * - Migrate to kotlin
 * - hide ripple when stop animation
 */
class RippleBackground(context: Context, val attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {

    private var rippleColor: Int = 0
    private var rippleStrokeWidth: Float = 0.toFloat()
    private var rippleRadius: Float = 0.toFloat()
    private var rippleDurationTime: Int = 0
    private var rippleAmount: Int = 0
    private var rippleDelay: Int = 0
    private var rippleScale: Float = 0.toFloat()
    private var rippleType: Int = 0
    private var paint: Paint = Paint()
    private var isRippleAnimationRunning = false
        private set
    private lateinit var animatorSet: AnimatorSet
    private val rippleViewList = ArrayList<RippleView>()

    init {
        init()
    }

    /**
     * Start Ripple Animation if isn't running
     */
    fun startRippleAnimation() {
        if (!isRippleAnimationRunning) {
            for (rippleView in rippleViewList) {
                rippleView.visibility = View.VISIBLE
            }
            animatorSet.start()
            isRippleAnimationRunning = true
        }
    }

    /**
     * Stop Ripple Animation if is running.
     */
    fun stopRippleAnimation() {
        if (isRippleAnimationRunning) {
            animatorSet.end()
            isRippleAnimationRunning = false
            for (rippleView in rippleViewList) {
                rippleView.visibility = View.INVISIBLE
            }

        }
    }

    private fun init() {
        if (isInEditMode) return

        if (null == attrs) {
            throw IllegalArgumentException("Attributes should be provided to this view,")
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground)
        rippleColor =
            typedArray.getColor(
                R.styleable.RippleBackground_rb_color,
                ContextCompat.getColor(context, R.color.rippelColor)
            )

        rippleStrokeWidth = typedArray.getDimension(
            R.styleable.RippleBackground_rb_strokeWidth,
            resources.getDimension(R.dimen.rippleStrokeWidth)
        )

        rippleRadius = typedArray.getDimension(
            R.styleable.RippleBackground_rb_radius,
            resources.getDimension(R.dimen.rippleRadius)
        )

        rippleDurationTime =
            typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME)
        rippleAmount =
            typedArray.getInt(R.styleable.RippleBackground_rb_rippleAmount, DEFAULT_RIPPLE_COUNT)
        rippleScale = typedArray.getFloat(R.styleable.RippleBackground_rb_scale, DEFAULT_SCALE)
        rippleType = typedArray.getInt(R.styleable.RippleBackground_rb_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()

        rippleDelay = rippleDurationTime / rippleAmount

        initAnimator()
    }

    private fun initAnimator() {
        paint = Paint().apply {
            isAntiAlias = true
            if (rippleType == DEFAULT_FILL_TYPE) {
                rippleStrokeWidth = 0f
                style = Paint.Style.FILL
            } else {
                style = Paint.Style.STROKE
            }
            color = rippleColor
        }

        val rippleParams = RelativeLayout.LayoutParams(
            (2 * (rippleRadius + rippleStrokeWidth)).toInt(),
            (2 * (rippleRadius + rippleStrokeWidth)).toInt()
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }

        val animatorList = mutableListOf<ObjectAnimator>()

        for (i in 0 until rippleAmount) {
            val rippleView = RippleView(context)
            addView(rippleView, rippleParams)
            rippleViewList.add(rippleView)

            val scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = (i * rippleDelay).toLong()
            animatorList.add(scaleXAnimator)

            val scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = (i * rippleDelay).toLong()
            animatorList.add(scaleYAnimator)

            val alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f)
            alphaAnimator.repeatCount = ObjectAnimator.INFINITE
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = (i * rippleDelay).toLong()
            animatorList.add(alphaAnimator)
        }

        animatorSet = AnimatorSet().apply {
            duration = rippleDurationTime.toLong()
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(animatorList.toList())
        }
    }

    private inner class RippleView(context: Context) : View(context) {

        init {
            this.visibility = View.INVISIBLE
        }

        override fun onDraw(canvas: Canvas) {
            val radius = Math.min(width, height) / 2
            canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius - rippleStrokeWidth, paint)
        }
    }

    companion object {
        private const val DEFAULT_RIPPLE_COUNT = 6
        private const val DEFAULT_DURATION_TIME = 3000
        private const val DEFAULT_SCALE = 6.0f
        private const val DEFAULT_FILL_TYPE = 0
    }
}
