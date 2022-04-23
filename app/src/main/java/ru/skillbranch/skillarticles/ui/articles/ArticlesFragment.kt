package ru.skillbranch.skillarticles.ui.articles

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ArticlesFragment :
    BaseFragment<ArticlesState, ArticlesViewModel, FragmentArticlesBinding>(R.layout.fragment_articles) {
    override val viewModel: ArticlesViewModel by activityViewModels()
    override val viewBinding: FragmentArticlesBinding by viewBinding(FragmentArticlesBinding::bind)
    private var articlesAdapter: ArticlesAdapter? = null

    override fun renderUi(data: ArticlesState) {
        articlesAdapter?.submitList(viewModel.articles.value)
    }

    override fun setupViews() {
        articlesAdapter = ArticlesAdapter(
            onClick = { articleItem -> viewModel.navigateToArticle(articleItem) },
            onToggleBookmark = { articleItem, isChecked ->
                viewModel.checkBookmark(
                    articleItem,
                    isChecked
                )
            }
        )
        viewBinding.rvArticles.apply {
            adapter = articlesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articlesAdapter = null
    }

    override fun observeViewModelData() {
        super.observeViewModelData()
        viewModel.articles.observe(viewLifecycleOwner) {
            articlesAdapter?.submitList(it)
        }
    }

}