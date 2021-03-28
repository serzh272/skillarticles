package ru.skillbranch.skillarticles.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.databinding.LayoutSubmenuBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {
    private lateinit var viewModel: ArticleViewModel
    lateinit var binding: ActivityRootBinding
    lateinit var bottomBarBinding: LayoutBottombarBinding
    lateinit var subMenuBinding: LayoutSubmenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRootBinding.inflate(layoutInflater)
        bottomBarBinding = LayoutBottombarBinding.bind(binding.bottombar)
        subMenuBinding = LayoutSubmenuBinding.bind(binding.submenu)
        super.onCreate(savedInstanceState)

        with(binding) {
            setContentView(root)
            setupToolbar()
        }
        setupBottomBar()
        setupSubmenu()
        val vmFactory = ViewModelFactory("0")
        viewModel = ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
        viewModel.observeState(this){
            renderUi(it)
        }
        viewModel.observeNotifications(this){
            renderNotification(it)
        }
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(binding.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.bottombar)
        when(notify){
            is Notify.TextMessage -> {}
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel){
                    notify.actionHandler.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar){
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel){
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

    private fun setupSubmenu() {
        subMenuBinding.btnTextUp.setOnClickListener{viewModel.handleUpText()}
        subMenuBinding.btnTextDown.setOnClickListener{viewModel.handleDownText()}
        subMenuBinding.switchMode.setOnClickListener{viewModel.handleNightMode()}
    }

    private fun setupBottomBar() {
        bottomBarBinding.btnLike.setOnClickListener{viewModel.handleLike()}
        bottomBarBinding.btnBookmark.setOnClickListener{viewModel.handleBookmark()}
        bottomBarBinding.btnSettings.setOnClickListener{viewModel.handleToggleMenu()}
        bottomBarBinding.btnShare.setOnClickListener{viewModel.handleShare()}
    }

    private fun renderUi(data: ArticleState) {
        with(bottomBarBinding){
            btnSettings.isChecked = data.isShowMenu
            if (data.isShowMenu) binding.submenu.open() else binding.submenu.close()
            btnLike.isChecked = data.isLike
            btnBookmark.isChecked = data.isBookmark
        }
        with(subMenuBinding){
            switchMode.isChecked = data.isDarkMode
            delegate.localNightMode =
                if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            if (data.isBigText){
                binding.tvTextContent.textSize = 18f
                btnTextUp.isChecked = true
                btnTextDown.isChecked = false
            }else{
                binding.tvTextContent.textSize = 14f
                btnTextUp.isChecked = false
                btnTextDown.isChecked = true
            }
            with(binding) {
                tvTextContent.text =
                    if (data.isLoadingContent) "loading..." else data.content.first() as String
                toolbar.title = data.title ?: "loading"
                toolbar.subtitle = data.category ?: "loading"
                if (data.categoryIcon != null) toolbar.logo = getDrawable(data.categoryIcon as Int)
            }
        }
        val action:MenuItem? =  binding.toolbar.menu.findItem(R.id.action_search)
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
        with(binding.toolbar) {
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
        searchView?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
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
        searchView?.setOnSearchClickListener{
            viewModel.handleSearchMode(true)
        }
        return super.onCreateOptionsMenu(menu)
    }
}