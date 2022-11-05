package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior
import kotlin.math.hypot


class Bottombar(baseContext: Context) :
    ViewGroup(ContextThemeWrapper(baseContext, R.style.ArticleBarsTheme), null, 0),
    CoordinatorLayout.AttachedBehavior {

    //sizes
    @Px
    private val iconSize = context.dpToIntPx(56)

    @Px
    private val iconPadding = context.dpToIntPx(16)
    private val iconTint = context.getColorStateList(R.color.tint_color)
    val minHeight: Int = iconSize

    //views
    val btnLike: CheckableImageView
    val btnBookmark: CheckableImageView
    val btnShare: AppCompatImageView
    val btnSettings: CheckableImageView

    private val searchBar: SearchBar
    val tvSearchResult
        get() = searchBar.tvSearchResult
    val btnResultUp
        get() = searchBar.btnResultUp
    val btnResultDown
        get() = searchBar.btnResultDown
    val btnSearchClose
        get() = searchBar.btnSearchClose

    var isSearchMode = false

    override fun getBehavior(): CoordinatorLayout.Behavior<Bottombar> {
        return BottombarBehavior()
    }

    init {
        id = R.id.bottombar
        layoutParams =
            CoordinatorLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                .apply {
                    gravity = Gravity.BOTTOM
                    insetEdge = Gravity.BOTTOM
                }
        val elev = dpToPx(4)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        elevation = elev
        materialBg.elevation = elevation
        background = materialBg
        btnLike = CheckableImageView(context).apply {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_states))
            imageTintList = iconTint
            setBackgroundResource(R.drawable.ripple)
            setPadding(iconPadding)

        }
        addView(btnLike)
        btnBookmark = CheckableImageView(context).apply {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark_states))
            imageTintList = iconTint
            setBackgroundResource(R.drawable.ripple)
            setPadding(iconPadding)
        }
        addView(btnBookmark)
        btnShare = AppCompatImageView(context).apply {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_share_black_24dp))
            isFocusable = true
            isClickable = true
            imageTintList = iconTint
            setBackgroundResource(R.drawable.ripple)
            setPadding(iconPadding)
        }
        addView(btnShare)
        btnSettings = CheckableImageView(context).apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_format_size_black_24dp
                )
            )
            imageTintList = iconTint
            setBackgroundResource(R.drawable.ripple)
            setPadding(iconPadding)
        }
        addView(btnSettings)
        searchBar = SearchBar().apply {
            isVisible = false

        }
        addView(searchBar)
    }

    override fun onSaveInstanceState(): Parcelable {
        val saveState = SavedState(super.onSaveInstanceState())
        saveState.ssIsSearchMode = isSearchMode
        return saveState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            isSearchMode = state.ssIsSearchMode
            searchBar.isVisible = isSearchMode
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val usedWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        measureChild(searchBar, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(usedWidth, iconSize)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth
        var usedWidth = left
        btnLike.layout(
            usedWidth,
            0,
            usedWidth + iconSize,
            iconSize
        )
        usedWidth += iconSize
        btnBookmark.layout(
            usedWidth,
            0,
            usedWidth + iconSize,
            iconSize
        )
        usedWidth += iconSize
        btnShare.layout(
            usedWidth,
            0,
            usedWidth + iconSize,
            iconSize
        )
        btnSettings.layout(
            right - iconSize,
            0,
            right,
            iconSize
        )
        searchBar.layout(
            left,
            0,
            right,
            iconSize
        )

    }

    fun setSearchState(isSearch: Boolean) {
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
        if (isSearchMode) animatedShowSearch()
        else animateHideSearch()
    }

    fun setSearchInfo(searchCount: Int = 0, position: Int = 0) {
        btnResultUp.isEnabled = searchCount > 0
        btnResultDown.isEnabled = searchCount > 0
        tvSearchResult.text =
            if (searchCount == 0) resources.getString(R.string.not_found_text) else "${position.inc()} of $searchCount"
        when (position) {
            0 -> btnResultUp.isEnabled = false
            searchCount.dec() -> btnResultDown.isEnabled = false
        }
    }

    private fun animatedShowSearch() {
        searchBar.isVisible = true
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())
        ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            0f,
            endRadius.toFloat()
        ).start()
    }

    private fun animateHideSearch() {
        btnLike.isVisible = true
        btnBookmark.isVisible = true
        btnShare.isVisible = true
        btnSettings.isVisible = true
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())
        val va = ViewAnimationUtils.createCircularReveal(
            searchBar,
            width,
            height / 2,
            endRadius.toFloat(),
            0f
        ).apply {
            doOnEnd {
                searchBar.isVisible = false

            }
            start()
        }
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsSearchMode: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            ssIsSearchMode = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte(if (ssIsSearchMode) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }

    }

    @SuppressLint("ViewConstructor")
    inner class SearchBar : ViewGroup(context, null, 0) {
        internal val btnSearchClose: AppCompatImageView
        internal val tvSearchResult: TextView
        internal val btnResultDown: AppCompatImageView
        internal val btnResultUp: AppCompatImageView

        @ColorInt
        private val iconColor = context.attrValue(R.attr.colorPrimary)
        private val iconTintSearch = ColorStateList.valueOf(iconColor)

        init {
            isVisible = false
            setBackgroundColor(context.getColor(R.color.color_on_article_bar))
            btnSearchClose = AppCompatImageView(context).apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_black_24dp))
                setPadding(iconPadding)
                imageTintList = iconTintSearch
                setBackgroundResource(R.drawable.ripple)
                isClickable = true
                isFocusable = true
            }
            addView(btnSearchClose)

            tvSearchResult = TextView(context).apply {
                text = resources.getString(R.string.not_found_text)
                textSize = 14f
                setTextColor(iconColor)
                setPaddingOptionally(left = iconPadding)
            }
            addView(tvSearchResult)

            btnResultDown = AppCompatImageView(context).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_keyboard_arrow_down_black_24dp
                    )
                )
                setPadding(iconPadding)
                imageTintList = iconTintSearch
                setBackgroundResource(R.drawable.ripple)
                isClickable = true
                isFocusable = true
            }
            addView(btnResultDown)

            btnResultUp = AppCompatImageView(context).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_keyboard_arrow_up_black_24dp
                    )
                )
                setPadding(iconPadding)
                imageTintList = iconTintSearch
                setBackgroundResource(R.drawable.ripple)
                isClickable = true
                isFocusable = true
            }
            addView(btnResultUp)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            measureChild(tvSearchResult, widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(width, iconSize)
        }

        override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val bodyWidth = r - l - paddingLeft - paddingRight
            val left = paddingLeft
            val right = paddingLeft + bodyWidth
            var usedWidth = left
            btnSearchClose.layout(
                usedWidth,
                0,
                iconSize,
                iconSize
            )
            usedWidth += iconSize
            tvSearchResult.layout(
                usedWidth,
                (iconSize - tvSearchResult.measuredHeight) / 2,
                usedWidth + tvSearchResult.measuredWidth,
                (iconSize + tvSearchResult.measuredHeight) / 2
            )
            btnResultDown.layout(
                right - 2 * iconSize,
                0,
                right - iconSize,
                iconSize
            )
            btnResultUp.layout(
                right - iconSize,
                paddingTop,
                right,
                iconSize
            )
        }
    }
}

