package ru.skillbranch.skillarticles.ui

import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.article.BottombarData
import ru.skillbranch.skillarticles.viewmodels.article.SubmenuData

interface IArticleView {
    fun setupSubmenu()
    fun setupBottomBar()
    fun renderBottombar(data: BottombarData)
    fun renderSubmenu(data: SubmenuData)
    fun renderUi(data: ArticleState)
    fun setupToolbar()
    fun renderSearchResult(searchResult: List<Pair<Int, Int>>)
    fun renderSearchPosition(searchPosition:Int, searchResult: List<Pair<Int, Int>>)
    fun clearSearchResult()
    fun showSearchBar(resultsCount: Int, searchPosition: Int)
    fun hideSearchBar()
    fun setupCopyListener()
}