package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.databinding.LayoutSubmenuBinding

class ArticleSubmenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0

): ConstraintLayout(context, attrs, defStyleAttr){
    var isOpen = false
    init {
        LayoutSubmenuBinding.inflate(LayoutInflater.from(context),this, true)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }
    fun open(){
        if (isOpen) return
        isOpen = true
        visibility = View.VISIBLE
    }
    fun close(){
        if (!isOpen) return
        isOpen = false
        visibility = View.GONE
        //TODO add reveal animation
    }
}