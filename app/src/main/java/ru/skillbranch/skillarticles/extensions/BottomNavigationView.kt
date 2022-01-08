package ru.skillbranch.skillarticles.extensions

import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.selectDestination(destination: NavDestination) {
    this.menu.forEach { item ->
        if (destination.matchDestination(item.itemId)) {
            item.isChecked = true
        }

        //implement select profile icon if auth flow is open
    }
}


fun NavDestination.matchDestination(@IdRes resId: Int) =
    hierarchy.any { it.id == resId }