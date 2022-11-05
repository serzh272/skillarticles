package ru.skillbranch.skillarticles.ui.article

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.material.appbar.AppBarLayout
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.network.res.CommentRes
import ru.skillbranch.skillarticles.databinding.FragmentArticleBinding
import ru.skillbranch.skillarticles.extensions.*
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.IArticleView
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.article.*


class ArticleFragment :
    BaseFragment<ArticleState, ArticleViewModel, FragmentArticleBinding>(R.layout.fragment_article),
    IArticleView {
    override val viewModel: ArticleViewModel by viewModels()
    override val viewBinding: FragmentArticleBinding by viewBinding(FragmentArticleBinding::bind)

    private lateinit var searchView: SearchView
    private lateinit var toolbar: Toolbar
    private lateinit var bottombar: Bottombar
    private lateinit var submenu: ArticleSubmenu

    private var commentAdapter: CommentAdapter? = null

    private val logoSize: Int by lazy { dpToIntPx(40) }
    private val cornerRadius: Int by lazy { dpToIntPx(8) }

    private val args: ArticleFragmentArgs by navArgs()

    override fun setupViews() {
        setupCopyListener()
        with(viewBinding) {
            Glide.with(this@ArticleFragment)
                .load(args.authorAvatar)
                .placeholder(R.drawable.logo_placeholder)
                .apply(circleCropTransform())
                .override(logoSize)
                .into(ivAuthorAvatar)
            Glide.with(this@ArticleFragment)
                .load(args.poster)
                .placeholder(R.drawable.poster_placeholder)
                .transform(CenterCrop(), RoundedCorners(cornerRadius))
                .into(ivPoster)
            tvTitle.text = args.title
            tvAuthor.text = args.author
            tvDate.text = args.date.format()

            etComment.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) onClickMessageSend()
                true
            }

            etComment.setOnFocusChangeListener { _, isFocused ->
                wrapComments.isEndIconVisible = isFocused
            }

            wrapComments.isEndIconVisible = false
            wrapComments.setEndIconOnClickListener {
                it.hideKeyboard()
                etComment.setText("")
                etComment.clearFocus()
                viewModel.answerTo(null)
            }

            with(rvComments) {
                val maxHeight = screenHeight() - wrapComments.height
                layoutParams = layoutParams.apply {
                    height = maxHeight
                }
                CommentAdapter(::onSelectComment)
                    .also { commentAdapter = it }
                    .run {
                        adapter = withLoadStateFooter(footer = LoadStateItemsAdapter(::retry))
                        layoutManager = LinearLayoutManager(requireContext())
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu.findItem(R.id.action_search)
        searchView = (menuItem?.actionView as SearchView)
        searchView.queryHint = getString(R.string.article_search_placeholder)
        if (viewModel.currentState.isSearch) {
            menuItem.expandActionView()
            searchView.setQuery(viewModel.currentState.searchQuery, false)
            searchView.requestFocus()
        } else {
            searchView.clearFocus()
        }
        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun setupActivityViews() {
        root.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        root.viewBinding.navView.isVisible = false
        toolbar = root.viewBinding.toolbar
        bottombar = Bottombar(requireContext())
        submenu = ArticleSubmenu(requireContext())
        root.viewBinding.coordinatorContainer.addView(bottombar)
        root.viewBinding.coordinatorContainer.addView(submenu)
        setupToolbar()
        setupBottomBar()
        setupSubmenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        root.viewBinding.navView.isVisible = true
        toolbar.logo = null
        toolbar.subtitle = null
        root.viewBinding.coordinatorContainer.removeView(bottombar)
        root.viewBinding.coordinatorContainer.removeView(submenu)
    }

    override fun observeViewModelData() {
        viewModel.observeSubState(this, ArticleState::toBottombarData, ::renderBottombar)
        viewModel.observeSubState(this, ArticleState::toSubmenuData, ::renderSubmenu)

        viewModel.observeSubState(viewLifecycleOwner, { it.answerName }, ::renderAnswerTo)

        viewModel.commentPager.observe(viewLifecycleOwner) {
            commentAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun setupSubmenu() {
        with(submenu) {
            btnTextUp.setOnClickListener { viewModel.handleUpText() }
            btnTextDown.setOnClickListener { viewModel.handleDownText() }
            switchMode.setOnClickListener { viewModel.handleNightMode() }
        }
    }

    override fun setupBottomBar() {
        with(bottombar) {
            btnLike.setOnClickListener { viewModel.handleLike() }
            btnBookmark.setOnClickListener { viewModel.handleBookmark() }
            btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
            btnShare.setOnClickListener { viewModel.handleShare() }
            btnResultUp.setOnClickListener {
                if (!viewBinding.tvTextContent.hasFocus()) {
                    viewBinding.tvTextContent.requestFocus()
                }
                it.hideKeyboard()
                viewModel.handleUpResult()
            }
            btnResultDown.setOnClickListener {
                if (!viewBinding.tvTextContent.hasFocus()) {
                    viewBinding.tvTextContent.requestFocus()
                }
                it.hideKeyboard()
                viewModel.handleDownResult()
            }
            btnSearchClose.setOnClickListener {
                viewModel.handleSearchMode(false)
                root.invalidateOptionsMenu()
            }
        }
    }

    override fun renderBottombar(data: BottombarData) {
        with(bottombar) {
            btnLike.isChecked = data.isLike
            btnBookmark.isChecked = data.isBookmark
            btnSettings.isChecked = data.isShowMenu
        }
        if (data.isSearch) {
            root.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            showSearchBar(data.resultsCount, data.searchPosition)
            with(toolbar) {
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                    AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
            }
        } else {
            root.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            hideSearchBar()
            with(toolbar) {
                (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
            }
        }
    }

    override fun renderSubmenu(data: SubmenuData) {
        with(submenu) {
            switchMode.isChecked = data.isDarkMode
            btnTextDown.isChecked = !data.isBigText
            btnTextUp.isChecked = data.isBigText
        }
        if (data.isShowMenu) submenu.open() else submenu.close()
    }

    override fun renderUi(data: ArticleState) {
        submenu.isVisible = false
        root.delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        with(viewBinding.tvTextContent) {
            textSize = if (data.isBigText) 18f else 14f
            isLoading = data.content.isEmpty()
            setContent(data.content)
        }
        if (data.isLoadingContent) return
        if (data.isSearch) {
            renderSearchResult(data.searchResults)
            renderSearchPosition(data.searchPosition, data.searchResults)
        } else clearSearchResult()
    }

    override fun setupToolbar() {
        toolbar.setLogo(R.drawable.logo_placeholder)
        val logo = toolbar.children.find { it is AppCompatImageView } as? ImageView
        logo ?: return
        logo.scaleType = ImageView.ScaleType.CENTER_CROP
        (logo.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = logoSize
            it.height = logoSize
            it.marginEnd = dpToIntPx(16)
            logo.layoutParams = it
        }

        toolbar.subtitle = args.category

        Glide.with(this)
            .load(args.categoryIcon)
            .apply(circleCropTransform())
            .override(logoSize)
            .into(logo)
    }

    override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        viewBinding.tvTextContent.renderSearchResult(searchResult)
    }

    override fun renderSearchPosition(searchPosition: Int, searchResult: List<Pair<Int, Int>>) {
        viewBinding.tvTextContent.renderSearchPosition(searchResult.getOrNull(searchPosition))
    }

    override fun clearSearchResult() {
        viewBinding.tvTextContent.clearSearchResult()
    }

    override fun showSearchBar(resultsCount: Int, searchPosition: Int) {
        with(bottombar) {
            setSearchState(true)
            setSearchInfo(resultsCount, searchPosition)
        }
        viewBinding.scroll.setMarginOptionally(bottom = dpToIntPx(56))
    }

    override fun hideSearchBar() {
        with(bottombar) {
            setSearchState(false)
        }
        viewBinding.scroll.setMarginOptionally(bottom = dpToIntPx(0))
    }

    override fun setupCopyListener() {
        viewBinding.tvTextContent.setCopyListener { copy ->
            val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
            val clip = ClipData.newPlainText("Copied code", copy)
            clipboard?.setPrimaryClip(clip)
            viewModel.handleCopyCode()
        }
    }

    override fun onClickMessageSend() {
        with(viewBinding.etComment) {
            hideKeyboard()
            clearFocus()
            viewModel.handleSendMessage(text.toString())
            setText("")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onSelectComment(comment: CommentRes) {
        with(viewBinding.wrapComments) {
            requestRectangleOnScreen(Rect(0, 0, width, height), false)
            postDelayed({
                viewBinding.etComment.showKeyboard()
            }, 300)
        }
        viewModel.answerTo(comment)
    }

    override fun renderAnswerTo(answerName: String?) {
        with(viewBinding.wrapComments) {
            hint = answerName?.let { "Answer to: $it" } ?: "Comment"
        }
    }
}