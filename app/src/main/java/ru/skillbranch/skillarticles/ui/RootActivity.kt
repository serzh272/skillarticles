package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.ui.custom.Bottombar
import ru.skillbranch.skillarticles.viewmodels.NavCommand
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.RootViewModel

class RootActivity : AppCompatActivity() {
    val viewModel: RootViewModel by viewModels()
    lateinit var viewBinding: ActivityRootBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        val appConfig = AppBarConfiguration(
            setOf(
                R.id.nav_articles,
                R.id.nav_bookmarks,
                R.id.nav_profile
            )
        )
        setupActionBarWithNavController(navController, appConfig)
        viewBinding.navView.setOnItemSelectedListener {
            viewModel.topLevelNavigate(it.itemId)
            true
        }

        viewModel.observeNavigation(this, ::handleNavigation)

        //viewBinding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewBinding.navView.selectDestination(destination)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun renderNotification(notify: Notify) {
        val snackbar =
            Snackbar.make(viewBinding.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
                .setAnchorView(findViewById<Bottombar>(R.id.bottombar) ?: viewBinding.navView)
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

    fun handleNavigation(cmd: NavCommand) {
        when (cmd) {
            is NavCommand.Action -> navController.navigate(cmd.action)
            is NavCommand.Builder -> navController.navigate(
                cmd.destination,
                cmd.args,
                cmd.options,
                cmd.extras
            )
            is NavCommand.TopLevel -> {
                val popBackstack = navController.popBackStack(cmd.destination, false)
                if (!popBackstack) navController.navigate(cmd.destination, null, cmd.options)
            }
        }
    }
}

fun BottomNavigationView.selectDestination(destination: NavDestination) {
    this.menu.forEach { item ->
        if (destination.matchDestination(item.itemId)) {
            item.isChecked = true
        }
    }
}


fun NavDestination.matchDestination(@IdRes resId: Int): Boolean {
    return hierarchy.any {
        it.id == resId
    }
}