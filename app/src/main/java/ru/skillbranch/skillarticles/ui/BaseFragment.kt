package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import ru.skillbranch.skillarticles.viewmodels.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.VMState

abstract class BaseFragment<S, T : BaseViewModel<S>, B : ViewBinding>(@LayoutRes layout: Int) :
    Fragment(layout), LifecycleEventObserver where S : VMState {
    protected val root
        get() = requireActivity() as RootActivity

    abstract val viewModel: T
    abstract val viewBinding: B
    abstract fun renderUi(data: S)
    abstract fun setupViews()

    open fun setupActivityViews() {
        //overwrite if need
    }

    open fun observeViewModelData() {
        //overwrite if need
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        root.lifecycle.addObserver(this)
        viewModel.observeNotifications(root, root::renderNotification)
        viewModel.observeNavigation(root, root::handleNavigation)
        viewModel.observeState(viewLifecycleOwner, ::renderUi)

        observeViewModelData()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> activityInflated()
            else -> {}
        }
    }

    private fun activityInflated() {
        root.viewBinding.appbar.setExpanded(true, true)
        setupActivityViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState()
    }
}