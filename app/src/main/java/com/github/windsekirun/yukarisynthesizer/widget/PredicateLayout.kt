package com.github.windsekirun.yukarisynthesizer.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.windsekirun.yukarisynthesizer.R
import com.google.android.flexbox.*
import java.util.*


class PredicateLayout<T> constructor(context: Context, attrs: AttributeSet? = null) : FlexboxLayout(context, attrs),
    View.OnClickListener {
    private val mItems = ArrayList<T>()
    private var mHorizontalSpacing = 1
    private var mVerticalSpacing = 1
    private var mTextSize = 0
    private var mTextColor = android.R.color.white
    private var mBackgroundDrawableRes: Int? = null
    private val mSelectedList = ArrayList<T>()
    private var mGravity = 0
    private var mClickListener: OnItemClickListener<T>? = null
    private var mTextTransformer: PredicateTextTransformer<T> = PredefindTextTransformer(context)
    private var mUsingCustomView = false
    private var mTransformer: ((T) -> View?)? = null

    init {
        init(attrs)
    }

    /**
     * add mItems into PredicateLayout
     *
     * @param items String...
     */
    fun addItem(vararg items: T) {
        this.mItems.addAll(items)
    }

    /**
     * set mItems into PredicateLayout
     *
     * @param items list of item
     */
    fun setItems(items: List<T>) {
        this.mItems.addAll(items)
    }

    /**
     * remove item in PredicateLayout
     *
     * @param item String
     */
    fun remove(item: T) {
        this.mItems.remove(item)
    }

    /**
     * Clear all layout
     */
    fun clear() {
        this.mItems.clear()
    }

    /**
     * notify when dataset is changed
     */
    fun notifyDataSetChanged() {
        removeAllViewsInLayout()
        for (item in mItems) {
            if (mUsingCustomView) {
                addView(getItemView(item))
            } else {
                addView(getItemTextView(item))
            }
        }
    }

    /**
     * set flag when using customview
     */
    fun usingCustomView(transformer: (T) -> View?) {
        mUsingCustomView = true
        mTransformer = transformer
    }

    /**
     * callback when click item
     */
    fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        this.mClickListener = listener
    }

    /**
     * callback when click item
     */
    fun setOnItemClickListener(listener: (T) -> Unit) {
        this.mClickListener = object : OnItemClickListener<T> {
            override fun onClick(item: T) {
                listener.invoke(item)
            }
        }
    }

    /**
     * get selected list
     */
    fun getSelectedList() = mSelectedList

    /**
     * set [PredicateTextTransformer] object
     *
     * PredicateTextTransformer is a tool to apply specific TextView style in PredicateLayout.
     * see [PredefindTextTransformer] for example.
     */
    fun setTextTransformer(transformer: PredicateTextTransformer<T>) {
        this.mTextTransformer = transformer
    }

    private fun getDimensionSize(resId: Int) = context.resources.getDimensionPixelSize(resId)

    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.predicate_layout, this)

        flexWrap = FlexWrap.WRAP
        flexDirection = FlexDirection.ROW
        alignItems = AlignItems.FLEX_START
        alignContent = AlignContent.FLEX_START
        setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE)
        dividerDrawableVertical = getDividerShape(mHorizontalSpacing)
        dividerDrawableHorizontal = getDividerShape(mVerticalSpacing)
    }

    private fun getDividerShape(px: Int): ShapeDrawable {
        return ShapeDrawable(RectShape()).apply {
            intrinsicWidth = px
            intrinsicHeight = px
            paint.color = Color.TRANSPARENT
        }
    }

    private fun getItemTextView(text: T): TextView {
        val gravity = getGravityValue()
        val textView =
            mTextTransformer.generateNewText(context, text, mBackgroundDrawableRes, mTextSize, gravity, getColor())
        textView.text = String.format(" %s ", text)
        textView.tag = text
        textView.setOnClickListener(this)
        return textView
    }

    private fun getItemView(text: T): View {
        if (mTransformer == null) {
            return getItemTextView(text)
        }

        return (mTransformer as (T) -> View?).invoke(text) ?: return getItemTextView(text)
    }

    private fun getColor() = ContextCompat.getColor(context, mTextColor)

    private fun getGravityValue(): Int {
        return when (mGravity) {
            0 -> Gravity.CENTER
            1 -> Gravity.START or Gravity.CENTER_VERTICAL
            2 -> Gravity.END or Gravity.CENTER_VERTICAL
            else -> Gravity.CENTER
        }
    }

    override fun onClick(v: View?) {
        val text = v?.tag as T

        if (mClickListener != null) {
            mClickListener?.onClick(text)
        }

        if (mSelectedList.contains(text))

            if (mSelectedList.contains(text)) {
                mSelectedList.remove(text)
            } else {
                mSelectedList.add(text)
            }
    }
}