package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.databinding.LayoutSubmenuBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.custom.CheckableImageView

class RootActivity : AppCompatActivity() {
    lateinit var binding: ActivityRootBinding
    lateinit var bottomBarBinding: LayoutBottombarBinding
    lateinit var subMenuBinding: LayoutSubmenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRootBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)

        //bottomBarBinding = LayoutBottombarBinding.inflate(LayoutInflater.from(binding.bottombar.context), binding.bottombar)
        with(binding) {
            bottomBarBinding = LayoutBottombarBinding.bind(bottombar)
            subMenuBinding = LayoutSubmenuBinding.bind(submenu)
            setContentView(root)
            setupToolbar()
            bottomBarBinding.btnLike.setOnClickListener {
                Snackbar.make(coordinatorContainer, "test", Snackbar.LENGTH_LONG)
                    .setAnchorView(bottombar)
                    .show()
            }
            subMenuBinding.switchMode.setOnClickListener {
                delegate.localNightMode =
                    if (subMenuBinding.switchMode.isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
            }
        }
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
}