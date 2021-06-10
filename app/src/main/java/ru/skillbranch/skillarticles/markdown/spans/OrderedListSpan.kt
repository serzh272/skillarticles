package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.Log
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
    private var paint = Paint()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override fun getLeadingMargin(first: Boolean): Int {
        val measureOrder = paint.measureText("$order.")
        return (2*gapWidth + measureOrder).toInt()
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        this.paint = paint
        if (isFirstLine){
            paint.forText {
                canvas.drawText("$order.", currentMarginLocation + gapWidth, lineBaseline.toFloat(),paint)
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