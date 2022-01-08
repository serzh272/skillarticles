package ru.skillbranch.skillarticles.ui.auth

import android.text.Spannable
import androidx.core.text.set
import androidx.navigation.navGraphViewModels
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentRegistrationBinding
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.custom.spans.UnderlineSpan
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.auth.AuthState
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel

class RegistrationFragment : BaseFragment<AuthState, AuthViewModel, FragmentRegistrationBinding>(
    R.layout.fragment_registration
) {
    override val viewModel: AuthViewModel by navGraphViewModels(R.id.auth_flow)
    override val viewBinding: FragmentRegistrationBinding by viewBinding(FragmentRegistrationBinding::bind)

    override fun renderUi(data: AuthState) {
        TODO("Not yet implemented")
    }

    override fun setupViews() {
        val decorColor = requireContext().attrValue(R.attr.colorPrimary)
        with(viewBinding) {
            tvPrivacy.setOnClickListener { viewModel.navigateToPrivacy() }
            (tvPrivacy.text as Spannable).let { it[0..it.length] = UnderlineSpan(decorColor) }
        }
    }
}