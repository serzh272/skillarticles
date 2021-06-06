package ru.skillbranch.skillarticles.markdown

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.CharacterStyle
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.markdown.spans.*

class MarkdownBuilder(context: Context) {
    private val cornerRadius = context.dpToPx(8)
    private val colorOnSurface = context.attrValue(R.attr.colorOnSurface)
    private val colorSurface = context.attrValue(R.attr.colorSurface)
    private val gap = context.dpToPx(8)
    private val bulletRadius = context.dpToPx(4)
    private val quoteWidth = context.dpToPx(4)
    private val colorSecondary = context.attrValue(R.attr.colorSecondary)
    private val colorPrimary = context.attrValue(R.attr.colorPrimary)
    private val colorDivider = context.getColor(R.color.color_divider)
    private val headerMarginTop = context.dpToPx(12)
    private val headerMarginBottom = context.dpToPx(8)
    private val ruleWidth = context.dpToPx(2)
    fun markdownToSpan(string: String) : SpannedString{
        val markdown = MarkdownParser.parse(string)
        return buildSpannedString {
            markdown.elements.forEach { buildElement(it, this) }
        }
    }

    private fun buildElement(element: Element, builder: SpannableStringBuilder): CharSequence {
        return builder.apply {
            when (element){
                is Element.Text -> append(element.text)
                is Element.UnorderedListItem -> {
                    inSpans(UnorderedListSpan(gap, bulletRadius, colorSecondary)){
                        for (child in element.elements){
                            buildElement(child, builder)
                        }
                    }
                }
                is Element.Quote -> {
                    inSpans(
                        BlockquotesSpan(gap, quoteWidth, colorSecondary),
                    StyleSpan(Typeface.ITALIC)){
                        for (child in element.elements){
                            buildElement(child, builder)
                        }
                    }
                }
                is Element.Header -> {
                    inSpans(HeaderSpan(element.level, colorPrimary, colorDivider, headerMarginTop, headerMarginBottom)){
                        append(element.text)
                    }
                }
                is Element.Bold ->{
                    inSpans(StyleSpan(Typeface.BOLD)){
                        for (child in element.elements){
                            buildElement(child, builder)
                        }
                    }
                }
                is Element.Italic ->{
                    inSpans(StyleSpan(Typeface.ITALIC)){
                        for (child in element.elements){
                            buildElement(child, builder)
                        }
                    }
                }
                is Element.Strike ->{
                    inSpans(StrikethroughSpan()){
                        for (child in element.elements){
                            buildElement(child, builder)
                        }
                    }
                }
                is Element.Rule ->{
                    inSpans(HorizontalRuleSpan(ruleWidth, colorDivider)){
                        append(element.text)
                    }
                }
                is Element.InlineCode ->{
                    inSpans(InlineCodeSpan(colorOnSurface, colorSurface,cornerRadius, gap)){
                        append(element.text)
                    }
                }
                else -> append(element.text)
            }
        }
    }
}