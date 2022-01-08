package ru.skillbranch.skillarticles.ui.articles

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.shortFormat
import ru.skillbranch.skillarticles.ui.custom.CheckableImageView
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem
import java.lang.Math.max

class ArticleItemView(baseContext: Context) : ViewGroup(baseContext, null, 0) {
    private val ivPoster: ImageView
    private val ivCategory: ImageView
    private val ivLikes: ImageView
    private val ivComments: ImageView
    private val ivBookmark: CheckableImageView
    private val tvDate: TextView
    private val tvAuthor: TextView
    private val tvTitle: TextView
    private val tvDescription: TextView
    private val tvLikesCount: TextView
    private val tvCommentsCount: TextView
    private val tvReadDuration: TextView

    private val defaultPadding = context.dpToIntPx(16)
    private val defaultSpace = context.dpToIntPx(8)
    private val cornerRadius = context.dpToIntPx(8)
    private val categorySize = context.dpToIntPx(40)
    private val posterSize = context.dpToIntPx(64)
    private val iconSize = context.dpToIntPx(16)
    private val grayColor = context.getColor(R.color.color_gray)
    private val primaryColor = context.attrValue(R.attr.colorPrimary)

    init {
        setPadding(defaultPadding)
        tvDate = TextView(baseContext).apply {
            id = R.id.tv_date
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tvDate)

        tvAuthor = TextView(baseContext).apply {
            id = R.id.tv_author
            setTextColor(primaryColor)
            textSize = 12f
        }
        addView(tvAuthor)

        tvTitle = TextView(baseContext).apply {
            id = R.id.tv_title
            setTextColor(primaryColor)
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
        }
        addView(tvTitle)

        ivPoster = ImageView(baseContext).apply {
            id = R.id.iv_poster
            layoutParams = LayoutParams(posterSize, posterSize)
        }
        addView(ivPoster)

        ivCategory = ImageView(baseContext).apply {
            id = R.id.iv_category
            layoutParams = LayoutParams(categorySize, categorySize)
        }
        addView(ivCategory)

        tvDescription = TextView(baseContext).apply {
            id = R.id.tv_description
            setTextColor(grayColor)
            textSize = 14f
        }
        addView(tvDescription)

        ivLikes = ImageView(baseContext).apply {
            id = R.id.iv_likes
            layoutParams = LayoutParams(iconSize, iconSize)
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.ic_favorite_black_24dp)
        }
        addView(ivLikes)

        tvLikesCount = TextView(baseContext).apply {
            id = R.id.tv_likes_count
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tvLikesCount)

        ivComments = ImageView(baseContext).apply {
            id = R.id.iv_comments
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.ic_insert_comment_black_24dp)
        }
        addView(ivComments)

        tvCommentsCount = TextView(baseContext).apply {
            id = R.id.tv_comments_count
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tvCommentsCount)

        tvReadDuration = TextView(baseContext).apply {
            id = R.id.tv_read_duration
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tvReadDuration)

        ivBookmark = CheckableImageView(baseContext).apply {
            id = R.id.iv_bookmark
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.bookmark_states)
        }
        addView(ivBookmark)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingBottom
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        measureChild(tvDate, widthMeasureSpec, heightMeasureSpec)
        tvAuthor.maxWidth = width - (tvDate.measuredWidth - 3 * defaultPadding)
        measureChild(tvAuthor, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvAuthor.measuredHeight
        val rh = posterSize + categorySize / 2
        tvTitle.maxWidth = width - (rh + 2 * paddingLeft + defaultSpace)
        measureChild(tvTitle, widthMeasureSpec, heightMeasureSpec)
        usedHeight += max(tvTitle.measuredHeight, rh) + 2 * defaultSpace

        measureChild(tvDescription, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvDescription.measuredHeight + defaultSpace
        measureChild(tvLikesCount, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvCommentsCount, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvReadDuration, widthMeasureSpec, heightMeasureSpec)

        usedHeight += iconSize + paddingBottom
        setMeasuredDimension(width, usedHeight)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        var usedHeight = paddingTop
        val bodyWidth = right - left - paddingLeft - paddingRight
        var left = paddingLeft

        tvDate.layout(
            left,
            usedHeight,
            left + tvDate.measuredWidth,
            usedHeight + tvDate.measuredHeight
        )
        left = tvDate.right + defaultPadding
        tvAuthor.layout(
            left,
            usedHeight,
            left + tvAuthor.measuredWidth,
            usedHeight + tvAuthor.measuredHeight
        )
        usedHeight += tvAuthor.measuredHeight + defaultSpace
        left = paddingLeft
        val rh = posterSize + categorySize / 2
        val leftTop = if (rh > tvTitle.measuredHeight) (rh - tvTitle.measuredHeight) / 2 else 0
        val rightTop = if (rh < tvTitle.measuredHeight) (tvTitle.measuredHeight - rh) / 2 else 0

        tvTitle.layout(
            left,
            usedHeight + leftTop,
            left + tvTitle.measuredWidth,
            usedHeight + leftTop + tvTitle.measuredHeight
        )

        ivPoster.layout(
            left + bodyWidth - posterSize,
            usedHeight + rightTop,
            left + bodyWidth,
            usedHeight + rightTop + posterSize
        )

        ivCategory.layout(
            ivPoster.left - categorySize / 2,
            ivPoster.bottom - categorySize / 2,
            ivPoster.left + categorySize / 2,
            ivPoster.bottom + categorySize / 2
        )

        usedHeight += if (rh > tvTitle.measuredHeight) rh else tvTitle.measuredHeight
        usedHeight += defaultSpace

        tvDescription.layout(
            left,
            usedHeight,
            left + bodyWidth,
            usedHeight + tvDescription.measuredHeight
        )
        usedHeight += tvDescription.measuredHeight + defaultSpace

        val fontDiff = iconSize - tvLikesCount.measuredHeight
        ivLikes.layout(
            left,
            usedHeight - fontDiff,
            left + iconSize,
            usedHeight + iconSize - fontDiff
        )

        left = ivLikes.right + defaultSpace
        tvLikesCount.layout(
            left,
            usedHeight,
            left + tvLikesCount.measuredWidth,
            usedHeight + tvLikesCount.measuredHeight
        )

        left = tvLikesCount.right + defaultPadding

        ivComments.layout(
            left,
            usedHeight - fontDiff,
            left + iconSize,
            usedHeight + iconSize - fontDiff
        )

        left = ivComments.right + defaultSpace
        tvCommentsCount.layout(
            left,
            usedHeight,
            left + tvCommentsCount.measuredWidth,
            usedHeight + tvCommentsCount.measuredHeight
        )
        left = defaultPadding
        ivBookmark.layout(
            left + bodyWidth + iconSize,
            usedHeight - fontDiff,
            left + bodyWidth,
            usedHeight + iconSize - fontDiff
        )


    }

    fun bind(
        item: ArticleItem,
        onClick: (ArticleItem) -> Unit,
        onToggleBookmark: (ArticleItem, Boolean) -> Unit
    ) {
        tvDate.text = item.date.shortFormat()
        tvAuthor.text = item.author
        tvTitle.text = item.title
        Glide.with(context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(ivPoster)
        Glide.with(context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(ivCategory)
        tvDescription.text = item.description
        tvLikesCount.text = "${item.likeCount}"
        tvCommentsCount.text = "${item.commentCount}"
        tvReadDuration.text = "${item.readDuration} min read"
        ivBookmark.isChecked = item.isBookmark
        setOnClickListener { onClick(item) }
        ivBookmark.setOnClickListener { onToggleBookmark(item, !item.isBookmark) }
    }
}
