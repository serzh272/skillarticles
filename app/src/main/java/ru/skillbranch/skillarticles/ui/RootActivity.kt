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
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {
    private lateinit var vb: ActivityRootBinding
    private val viewModel: ArticleViewModel by viewModels { ViewModelFactory("0") }
    private val vbBottomBar
        get() = vb.bottombar.binding
    private val vbSubmenu
        get() = vb.submenu.binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityRootBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupToolbar()
        setupBottomBar()
        setupSubmenu()
        viewModel.observeState(this) {
            renderUi(it)
        }
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

    private fun setupSubmenu() {

        vbSubmenu.btnTextUp.setOnClickListener { viewModel.handleUpText() }
        vbSubmenu.btnTextDown.setOnClickListener { viewModel.handleDownText() }
        vbSubmenu.switchMode.setOnClickListener { viewModel.handleNightMode() }
    }

    private fun setupBottomBar() {
        vbBottomBar.btnLike.setOnClickListener { viewModel.handleLike() }
        vbBottomBar.btnBookmark.setOnClickListener { viewModel.handleBookmark() }
        vbBottomBar.btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
        vbBottomBar.btnShare.setOnClickListener { viewModel.handleShare() }
    }

    private fun renderUi(data: ArticleState) {
        vbBottomBar.btnSettings.isChecked = data.isShowMenu
        if (data.isShowMenu) vb.submenu.open() else vb.submenu.close()
        vbBottomBar.btnLike.isChecked = data.isLike
        vbBottomBar.btnBookmark.isChecked = data.isBookmark
        vbSubmenu.switchMode.isChecked = data.isDarkMode
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        if (data.isBigText) {
            vb.tvTextContent.textSize = 18f
            vbSubmenu.btnTextUp.isChecked = true
            vbSubmenu.btnTextDown.isChecked = false
        } else {
            vb.tvTextContent.textSize = 14f
            vbSubmenu.btnTextUp.isChecked = false
            vbSubmenu.btnTextDown.isChecked = true
        }
        vb.tvTextContent.text =
            if (data.isLoadingContent) "loading..." else data.content.first() as String
        vb.toolbar.title = data.title ?: "loading"
        vb.toolbar.subtitle = data.category ?: "loading"
        if (data.categoryIcon != null) vb.toolbar.logo = getDrawable(data.categoryIcon as Int)
        val action: MenuItem? = vb.toolbar.menu.findItem(R.id.action_search)
        val searchView = action?.actionView as? SearchView
        if (data.isSearch) {
            action?.expandActionView()
            searchView?.clearFocus()
        } else {
            action?.collapseActionView()
        }
        searchView?.setQuery(data.searchQuery, false)
    }

    private fun setupToolbar() {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            R.id.action_search -> viewModel.handleSearchMode(true)
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val action = menu?.findItem(R.id.action_search)
//        action?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
//                viewModel.handleSearchMode(true)
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
//                viewModel.handleSearchMode(false)
//                return true
//            }
//
//        })
        val searchView = action?.actionView as? SearchView
        searchView?.queryHint = "Search"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) return false
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) return false
                viewModel.handleSearch(newText)
                return true
            }
        })
        searchView?.setOnCloseListener {
            viewModel.handleSearchMode(false)
            true
        }
        searchView?.setOnSearchClickListener {
            viewModel.handleSearchMode(true)
        }
        return super.onCreateOptionsMenu(menu)
    }
}