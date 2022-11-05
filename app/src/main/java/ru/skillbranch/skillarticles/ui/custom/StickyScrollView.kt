package ru.skillbranch.skillarticles.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.widget.NestedScrollView
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.screenHeight

class StickyScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {
    @IdRes
    private val targetId: Int

    @Px
    private val threshold: Int
    private lateinit var stickyView: View
    private val screenH = screenHeight()
    private var stickyState: StickyState = StickyState.IDLE

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.StickyScrollView,
            0,
            0
        ).apply {
            try {
                targetId = getResourceId(R.styleable.StickyScrollView_stickyView, -1)
                threshold = getDimensionPixelSize(R.styleable.StickyScrollView_threshold, 0)
            } finally {
                recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        stickyView = findViewById(targetId)
    }

    override fun onScrollChanged(l: Int, top: Int, oldl: Int, oldtop: Int) {
        super.onScrollChanged(l, top, oldl, oldtop)
        val isScrollDown = top - oldtop > 0
        val topEdge = threshold + top
        val bottomEdge = screenH - threshold + top

        when {
            bottomEdge < stickyView.top -> stickyState = StickyState.IDLE
            isScrollDown && bottomEdge > stickyView.top -> stickyState = StickyState.TOP
            !isScrollDown && topEdge < stickyView.bottom -> stickyState = StickyState.BOTTOM
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            post { animateState() }
        }
        return super.onTouchEvent(ev)
    }

    private fun animateState() {
        val y = when (stickyState) {
            StickyState.TOP -> stickyView.top
            StickyState.BOTTOM -> stickyView.top - screenH
            StickyState.IDLE -> return
        }
        smoothScrollTo(0, y)
    }

    private enum class StickyState {
        TOP, BOTTOM, IDLE
    }
}