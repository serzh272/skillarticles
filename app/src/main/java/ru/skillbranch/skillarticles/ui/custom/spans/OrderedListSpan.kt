package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting


class OrderedListSpan(
    @Px
    private val gapWidth: Float,
    private val order: String,
    @ColorInt
    private val orderColor: Int
) : LeadingMarginSpan {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override fun getLeadingMargin(first: Boolean): Int {
        //val measureOrder = paint.measureText("$order.")
        return (order.length.inc()*gapWidth).toInt()
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        if (isFirstLine){
            paint.forText {
                canvas.drawText(order, currentMarginLocation + gapWidth, lineBaseline.toFloat(),paint)
            }
        }

    }
    private inline fun Paint.forText(block: () -> Unit){
        val oldColor = color
        color = orderColor
        block()
        color = oldColor
    }
}