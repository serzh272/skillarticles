package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.network.res.CommentRes
import ru.skillbranch.skillarticles.extensions.*
import java.lang.Integer.min

class CommentItemView(context: Context) : ViewGroup(context, null, 0) {
    private val defaultVSpace = dpToIntPx(8)
    private val defaultHSpace = dpToIntPx(16)
    private val avatarSize = dpToIntPx(40)
    private val lineSize = dpToPx(2)
    private val iconSize = dpToIntPx(12)

    private val tvDate: TextView
    private val ivAvatar: ImageView
    private val tvAuthor: TextView
    private val tvMessage: TextView
    private val ivAnswerIcon: ImageView
    private val tvAnswerTo: TextView

    private val grayColor = getColor(context, R.color.color_gray)
    private val primaryColor = attrValue(R.attr.colorPrimary)
    private val dividerColor = getColor(context, R.color.color_divider)
    private val baseColor = getColor(context, R.color.color_gray_light)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dividerColor
        strokeWidth = lineSize
        style = Paint.Style.STROKE
    }

    init {
        setPadding(defaultHSpace, defaultVSpace, defaultHSpace, defaultVSpace)
        tvDate = TextView(context).apply {
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tvDate)
        ivAvatar = ImageView(context)
        addView(ivAvatar)
        tvAuthor = TextView(context).apply {
            setTextColor(primaryColor)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        }
        addView(tvAuthor)
        tvMessage = TextView(context).apply {
            id = R.id.tv_comment_body
            setTextColor(grayColor)
            textSize = 14f
        }
        addView(tvMessage)

        tvAnswerTo = TextView(context).apply {
            textSize = 12f
            isVisible = false
        }
        addView(tvAnswerTo)
        ivAnswerIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_baseline_reply_24)
            imageTintList = ColorStateList.valueOf(grayColor)
        }
        addView(ivAnswerIcon)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)


        measureChild(tvAnswerTo, widthMeasureSpec, heightMeasureSpec)
        if (tvAnswerTo.isVisible) usedHeight += tvAnswerTo.measuredHeight
        tvDate.minWidth = avatarSize
        measureChild(tvDate, widthMeasureSpec, heightMeasureSpec)
        tvAuthor.width =
            width - paddingLeft - paddingRight - avatarSize - defaultHSpace - tvDate.measuredWidth
        measureChild(tvAuthor, widthMeasureSpec, heightMeasureSpec)
        usedHeight += avatarSize + defaultVSpace
        tvMessage.width = width - paddingLeft - paddingRight
        measureChild(tvMessage, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvMessage.measuredHeight + defaultVSpace

        setMeasuredDimension(width, usedHeight)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        var usedHeight = paddingTop
        val left = paddingLeft

        if (tvAnswerTo.isVisible) {
            val lb = left + avatarSize + defaultHSpace / 2
            tvAnswerTo.layout(
                lb,
                usedHeight,
                lb + tvAnswerTo.measuredWidth,
                usedHeight + tvAnswerTo.measuredHeight
            )

            val diff = (tvAnswerTo.measuredHeight - iconSize) / 2
            ivAnswerIcon.layout(
                tvAnswerTo.right + defaultHSpace / 2,
                usedHeight + diff,
                tvAnswerTo.right + defaultHSpace / 2 + iconSize,
                usedHeight + iconSize + diff
            )
            usedHeight += tvAnswerTo.measuredHeight
        }

        val diffH = (avatarSize - tvAuthor.measuredHeight) / 2
        val diffD = (avatarSize - tvDate.measuredHeight) / 2

        ivAvatar.layout(
            left,
            usedHeight,
            left + avatarSize,
            usedHeight + avatarSize
        )
        tvAuthor.layout(
            ivAvatar.right + defaultHSpace / 2,
            usedHeight + diffH,
            ivAvatar.right + defaultHSpace / 2 + tvAuthor.measuredWidth,
            usedHeight + tvAuthor.measuredHeight + diffH
        )

        tvDate.layout(
            tvAuthor.right + defaultHSpace / 2,
            usedHeight + diffH,
            tvAuthor.right + defaultHSpace / 2 + tvDate.measuredWidth,
            usedHeight + tvDate.measuredHeight + diffD
        )
        usedHeight += avatarSize + defaultVSpace

        tvMessage.layout(
            left,
            usedHeight,
            left + tvMessage.measuredWidth,
            usedHeight + tvMessage.measuredHeight
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val level = min(paddingLeft / defaultHSpace, 5)
        if (level == 1) return
        for (i in 1 until level) {
            canvas.drawLine(
                i.toFloat() * defaultHSpace,
                0f,
                i.toFloat() * defaultHSpace,
                canvas.height.toFloat(),
                linePaint
            )
        }
    }

    fun bind(item: CommentRes) {
        val level = min(item.slug.split("/").size.dec(), 5)
        setPaddingOptionally(left = level * defaultHSpace)

        Glide.with(context)
            .load(item.user.avatar)
            .apply(RequestOptions.circleCropTransform())
            .override(avatarSize)
            .into(ivAvatar)

        tvAuthor.text = item.user.name
        tvDate.text = item.date.humanizeDiff()
        tvMessage.text = item.message
        tvAnswerTo.text = item.answerTo
        tvAnswerTo.isVisible = item.answerTo != null
        ivAnswerIcon.isVisible = item.answerTo != null
    }

}
