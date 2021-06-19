package ru.skillbranch.skillarticles.markdown

import java.lang.StringBuilder
import java.util.regex.Pattern

object MarkdownParser {
    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    //group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[+*-]\\s.+?$)"
    private const val ORDERED_LIST_ITEM_GROUP = "(^\\d\\.\\s.+?$)"
    private const val HEADER_GROUP = "(^#{1,6}\\s.+?$)"
    private const val QUOTE_GROUP = "(^>\\s.+?$)"
    private const val ITALIC_GROUP =
        "((?<!\\*)\\*[^\\*].+?[^\\*]?\\*(?!\\*)|(?<!_)_[^_].+?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^\\*].+?\\*{2}(?!\\*)|(?<!_)_{2}[^_].+?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~]+?~{2})"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]+?\\][^\\[\\]\\(]?\\([^\\(\\)]+?\\))"
    private const val IMAGE_GROUP = "(!\\[[^\\]\\[]*?\\]\\([^\\(]*?\\))"
    //private const val BLOCK_GROUP = "(^`{3}[^(`{3})\\n]+\\n?(.*\\n)*?`{3})" //правильный паттерн
    //private const val BLOCK_GROUP = "(^`{3}(.|\\n)*?\\n?`{3}(?=\\n))" //неправильная разметка урока
    private const val BLOCK_GROUP = "(^`{3}[\\s\\S]+?\\n?`{3}\$)" //Короткий вариант


    //result regex
    const val MARKDOWN_GROUPS =
        "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP|$ITALIC_GROUP|$BOLD_GROUP|" +
                "$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP|$ORDERED_LIST_ITEM_GROUP|$IMAGE_GROUP|" +
                "$BLOCK_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    /**
     * clear markdown text to string without markdown characters
     */
    fun clear(string: String): String? {
        string ?: return null
        return parse(string).elements.spread().joinToString(separator = "") { it.clearContent() }
    }

    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0
        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()
            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }
            var text: CharSequence
            val groups = 1..12
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                -1 -> break@loop
                //UnorderedListItem
                1 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //Header
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //Quote
                3 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements = findElements(text)
                    val element = Element.Quote(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //Italic
                4 -> {
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)
                    val element = Element.Italic(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //Bold
                5 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Bold(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //Strike
                6 -> {
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Strike(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //RULE
                7 -> {
                    val element = Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //INLINE CODE
                8 -> {
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element = Element.InlineCode(text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                //LINK_GROUP
                9 -> {
                    text = string.subSequence(startIndex, endIndex)
                    val (title: String, link:String) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                //ORDERED_LIST
                10 -> {
                    text = string.subSequence(startIndex, endIndex)
                    val (order: String, item:String) = "^(\\d+\\.)\\s(.*)$".toRegex().find(text)!!.destructured

                    val element = Element.OrderedListItem(order, item)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                11 -> {
                    text = string.subSequence(startIndex, endIndex)
                    val alt:String? = "!\\[(.+)]\\(.*\\)".toRegex().find(text)?.groupValues?.get(1)
                    val url:String = "!\\[.*]\\((.+?)[\\s\\)]".toRegex().find(text)?.groupValues?.get(1) ?: ""
                    val title:String = "!\\[.*]\\(.+?\\s\"(.*?)\"\\)".toRegex().find(text)?.groupValues?.get(1) ?: ""
                    val element = Element.Image(url, alt, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                12 -> {
                    //text = string.subSequence(startIndex.plus(3), endIndex.minus(3))
                    text = string.subSequence(startIndex.plus(3), endIndex)

                    val element = Element.BlockCode(text.replace("\\n?`{3}".toRegex(), ""))
                    parents.add(element)
                    lastStartIndex = endIndex
                }

            }
        }

        if (lastStartIndex < string.length){
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }
        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element() {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ",
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link:String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order:String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Image(
        val url:String,
        val alt:String?,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ):Element()


}
private fun Element.spread(): List<Element>{
    val elements = mutableListOf<Element>()
    if (this.elements.isNotEmpty()) elements.addAll(this.elements.spread())
    else elements.add(this)
    return elements
}

private fun List<Element>.spread(): List<Element>{
    val elements = mutableListOf<Element>()
    forEach { elements.addAll(it.spread()) }
    return elements
}

private fun Element.clearContent(): String{
    return StringBuilder().apply {
        val element = this@clearContent
        if (element.elements.isEmpty()) append(element.text)
        else element.elements.forEach { append(it.clearContent()) }
    }.toString()
}

fun MarkdownText.clearContent(): String{
    return StringBuilder().apply {
        elements.forEach {
            if (it.elements.isEmpty()) append(it.text)
            else it.elements.forEach { el -> append(el.clearContent()) }
        }
    }.toString()
}