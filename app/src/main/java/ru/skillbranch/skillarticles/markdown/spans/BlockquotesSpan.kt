package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px

class BlockquotesSpan(
    @Px
    private val gapWidth: Float,
    @Px
    private val quoteWidth: Float,
    @ColorInt
    private val color: Int
) : LeadingMarginSpan {
    override fun getLeadingMargin(first: Boolean): Int {

        return (quoteWidth + gapWidth).toInt()
    }

    override fun drawLeadingMargin(
        c: Canvas?,
        p: Paint?,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        p?.withCustomColor {
            //c?.drawCircle(gapWidth + x + bulletRadius, (bottom + top) / 2f, bulletRadius, p!!)
            c?.drawLine(
                quoteWidth / 2f,
                top.toFloat(),
                quoteWidth / 2f,
                bottom.toFloat(),
                p
            )
        }
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWidth = strokeWidth
        color = this@BlockquotesSpan.color
        style = Paint.Style.STROKE
        strokeWidth = quoteWidth
        block()
        this.color = oldColor
        style = oldStyle
        strokeWidth = oldWidth
    }

}