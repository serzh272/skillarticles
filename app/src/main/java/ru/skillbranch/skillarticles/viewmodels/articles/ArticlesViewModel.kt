package ru.skillbranch.skillarticles.viewmodels.articles

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptions
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.NavCommand
import ru.skillbranch.skillarticles.viewmodels.VMState

class ArticlesViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel<ArticlesState>(
    ArticlesState(), savedStateHandle
) {
    private val repository: ArticlesRepository = ArticlesRepository()
    val articles: LiveData<List<ArticleItem>> = repository.findArticles()

    fun navigateToArticle(articleItem: ArticleItem) {
        articleItem.run {
            val options = NavOptions.Builder()
                .setEnterAnim(R.animator.nav_default_enter_anim)
                .setExitAnim(R.animator.nav_default_exit_anim)
                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
            navigate(
                NavCommand.Builder(
                    R.id.page_article,
                    bundleOf(
                        "article_id" to id,
                        "author" to author,
                        "author_avatar" to authorAvatar,
                        "category" to category,
                        "category_icon" to categoryIcon,
                        "poster" to poster,
                        "title" to title,
                        "date" to date
                    ),
                    options.build()
                )
            )
        }
    }

    fun checkBookmark(articleItem: ArticleItem, checked: Boolean) {

    }
}

data class ArticlesState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true,
    val isBookmark: Boolean = false,
    val selectedCategories: List<String> = emptyList(),
    val isHashtagSearch: Boolean = false,
    val tags: List<String> = emptyList()
) : VMState