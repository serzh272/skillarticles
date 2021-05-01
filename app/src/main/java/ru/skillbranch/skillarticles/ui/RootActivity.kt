package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.*

class RootActivity : AppCompatActivity(), IArticleView {
    private val vb: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)
    private val viewModel: ArticleViewModel by viewModels { ViewModelFactory("0") }
    private val vbBottomBar
        get() = vb.bottombar.binding
    private val vbSubmenu
        get() = vb.submenu.binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        setupToolbar()
        setupBottomBar()
        setupSubmenu()
        viewModel.observeState(this, ::renderUi)
        viewModel.observeSubState(this, ArticleState::toBottomBarData, ::renderBottomBar)
        viewModel.observeSubState(this, ArticleState::toSubMenuData, ::renderSubmenu)
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(vb.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(vb.bottombar)
        when (notify) {
            is Notify.TextMessage -> {
            }
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

    override fun setupSubmenu() {
        with(vbSubmenu) {
            btnTextUp.setOnClickListener { viewModel.handleUpText() }
            btnTextDown.setOnClickListener { viewModel.handleDownText() }
            switchMode.setOnClickListener { viewModel.handleNightMode() }
        }
    }

    override fun setupBottomBar() {
        with(vbBottomBar) {
            btnLike.setOnClickListener { viewModel.handleLike() }
            btnBookmark.setOnClickListener { viewModel.handleBookmark() }
            btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
            btnShare.setOnClickListener { viewModel.handleShare() }
        }
    }

    override fun renderBottomBar(data: BottombarData) {
        with(vbBottomBar){
            btnLike.isChecked = data.isLike
            btnBookmark.isChecked = data.isBookmark
            btnSettings.isChecked = data.isShowMenu
        }
        if (data.isSearch) showSearchBar(data.resultsCount, data.searchPosition)
        else hideSearchBar()
    }

    override fun renderSubmenu(data: SubmenuData) {
        with(vbSubmenu){
            switchMode.isChecked = data.isDarkMode
            btnTextDown.isChecked = !data.isBigText
            btnTextUp.isChecked = data.isBigText
        }
        if (data.isShowMenu) vb.submenu.open() else vb.submenu.close()
    }

    override fun renderUi(data: ArticleState) {

        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        with(vb.tvTextContent){
            textSize = if(data.isBigText) 18f else 14f
            text = if(data.isLoadingContent) "loading" else data.content.first()
            //TODO set content as spannable
        }
        vb.toolbar.title = data.title ?: "loading"
        vb.toolbar.subtitle = data.category ?: "loading"
        if (data.categoryIcon != null) vb.toolbar.logo = getDrawable(data.categoryIcon as Int)
    }

    override fun setupToolbar() {
        with(vb.toolbar) {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val logo = if (childCount > 2) this.getChildAt(2) as ImageView else null
            logo?.scaleType = ImageView.ScaleType.CENTER_CROP
            println(logo?.id)
            val lp = logo?.layoutParams as? Toolbar.LayoutParams
            lp?.let {
                it.width = this@RootActivity.dpToIntPx(40)
                it.height = this@RootActivity.dpToIntPx(40)
                it.marginEnd = this@RootActivity.dpToIntPx(16)
                logo.layoutParams = it
            }
        }
    }

    override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        //TODO("Not yet implemented")
    }

    override fun renderSearchPosition(searchPosition: Int) {
        //TODO("Not yet implemented")
    }

    override fun clearSearchResult() {
        //TODO("Not yet implemented")
    }

    override fun showSearchBar(resultsCount: Int, searchPosition: Int) {
        with(vb.bottombar){
            setSearchState(true)
            setSearchInfo(resultsCount, searchPosition)
        }
        vb.scroll.setMarginOptionally(bottom = dpToIntPx(56))
    }

    override fun hideSearchBar() {
        with(vb.bottombar){
            setSearchState(false)
        }
        vb.scroll.setMarginOptionally(bottom = dpToIntPx(0))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu?.findItem(R.id.action_search)

        val searchView = menuItem?.actionView as? SearchView
        searchView?.queryHint = getString(R.string.article_search_placeholder)

        if (viewModel.currentState.isSearch) {
            menuItem?.expandActionView()
            searchView?.setQuery(viewModel.currentState.searchQuery, false)
            searchView?.requestFocus()
        } else {
            searchView?.clearFocus()
        }

        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}