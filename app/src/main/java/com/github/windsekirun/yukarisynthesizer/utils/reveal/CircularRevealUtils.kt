package com.github.windsekirun.yukarisynthesizer.utils.reveal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.yukarisynthesizer.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Animation utils for circular reveal animation
 * Original post: https://medium.com/@gabornovak/circular-reveal-animation-between-fragments-d8ed9011aec
 *
 * @FIXME 2018-11-30, WindSekirun
 *  - Port to Kotlin, AndroidX
 *  - Use doOnLayout instead View.OnLayoutChangeListener
 *  - Use ActivityReference instead provided Context object
 */
object CircularRevealUtils {

    /**
     * start Reveal animation when enter to
     *
     * @param view target
     * @param revealSettings [RevealSetting] for hold position of container and fab
     * @param finished optional, callback when animation is done.
     */
    @JvmStatic
    fun revealEnter(
        view: View,
        revealSettings: RevealSetting,
        finished: (() -> Unit)? = null
    ) {
        startEnterAnimation(
            view,
            revealSettings,
            getColor(R.color.title_color),
            getColor(R.color.white),
            finished
        )
    }

    /**
     * start Reveal animation when exit from View
     *
     * @param view target
     * @param revealSettings [RevealSetting] for hold position of container and fab
     * @param finished optional,  callback when animation is done.
     */
    @JvmStatic
    fun revealExit(
        view: View,
        revealSettings: RevealSetting,
        finished: (() -> Unit)? = null
    ) {
        startExitAnimation(
            view,
            revealSettings,
            getColor(R.color.white),
            getColor(R.color.title_color),
            finished
        )
    }

    private fun getMediumDuration(): Int {
        val context = ActivityReference.getContext()
        return context.resources.getInteger(android.R.integer.config_mediumAnimTime)
    }

    @ColorInt
    private fun getColor(@ColorRes colorId: Int): Int {
        val context = ActivityReference.getContext()
        return ContextCompat.getColor(context, colorId)
    }

    private fun startEnterAnimation(
        view: View,
        revealSettings: RevealSetting,
        startColor: Int, endColor: Int, finished: (() -> Unit)? = null
    ) {
        view.doOnLayout {
            val cx = revealSettings.centerX
            val cy = revealSettings.centerY
            val width = revealSettings.width
            val height = revealSettings.height

            val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(it, cx, cy, 0f, finalRadius)
            anim.duration = getMediumDuration()
                .toLong()
            anim.interpolator = FastOutSlowInInterpolator()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    finished?.invoke()
                }
            })
            anim.start()
            startBackgroundColorAnimation(
                view,
                startColor,
                endColor,
                getMediumDuration()
            )
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startExitAnimation(
        view: View,
        revealSettings: RevealSetting,
        startColor: Int, endColor: Int, finished: (() -> Unit)? = null
    ) {
        val cx = revealSettings.centerX
        val cy = revealSettings.centerY
        val width = revealSettings.width
        val height = revealSettings.height

        val initRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f)

        anim.duration = getMediumDuration().toLong()
        anim.interpolator = FastOutSlowInInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
                finished?.invoke()
            }
        })
        anim.start()
        startBackgroundColorAnimation(
            view,
            startColor,
            endColor,
            getMediumDuration()
        )
    }

    private fun startBackgroundColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.duration = duration.toLong()
        anim.addUpdateListener { valueAnimator -> view.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.start()
    }

    data class RevealSetting(var centerX: Int, var centerY: Int, var width: Int, var height: Int) {
        companion object {
            fun with(fab: FloatingActionButton, container: View): RevealSetting =
                RevealSetting(
                    (fab.x + (fab.width / 2)).toInt(), (fab.y + (fab.height / 2)).toInt(),
                    container.width, container.height
                )
        }
    }
}