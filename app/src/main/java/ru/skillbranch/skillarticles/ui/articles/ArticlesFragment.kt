package ru.skillbranch.skillarticles.ui.articles

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.article.LoadStateItemsAdapter
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ArticlesFragment :
    BaseFragment<ArticlesState, ArticlesViewModel, FragmentArticlesBinding>(R.layout.fragment_articles) {
    override val viewModel: ArticlesViewModel by activityViewModels()
    override val viewBinding: FragmentArticlesBinding by viewBinding(FragmentArticlesBinding::bind)
    private var articlesAdapter: ArticlesAdapter? = null

    override fun renderUi(data: ArticlesState) {
        //TODO implement me later
    }

    override fun setupViews() {
        with(viewBinding) {
            with(rvArticles) {
                ArticlesAdapter(::onArticleClick, ::onToggleBookmark)
                    .also { articlesAdapter = it }
                    .run {
                        adapter = withLoadStateFooter(footer = LoadStateItemsAdapter(::retry))
                        layoutManager = LinearLayoutManager(requireContext())
                        addItemDecoration(
                            DividerItemDecoration(
                                requireContext(),
                                LinearLayoutManager.VERTICAL
                            )
                        )
                        addLoadStateListener(::loadStateListener)
                    }
            }
            btnRetry.setOnClickListener {
                articlesAdapter?.retry()
            }
        }
    }

    private fun loadStateListener(loadStates: CombinedLoadStates) {
        with(viewBinding) {
            val isLoading = loadStates.refresh is LoadState.Loading
            val isError = loadStates.refresh is LoadState.Error
            val isSuccess = !isLoading && !isError

            rvArticles.isVisible = isSuccess
            progress.isVisible = isLoading
            groupErr.isVisible = isError
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articlesAdapter = null
    }

    override fun observeViewModelData() {
        viewModel.articlesPager.observe(viewLifecycleOwner) {
            articlesAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun onArticleClick(articleItem: ArticleItem) {
        viewModel.navigateToArticle(articleItem)
    }

    private fun onToggleBookmark(articleItem: ArticleItem, isChecked: Boolean) {
        viewModel.checkBookmark(articleItem, isChecked)
    }

}