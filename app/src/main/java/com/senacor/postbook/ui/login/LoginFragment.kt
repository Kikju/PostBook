package com.senacor.postbook.ui.login

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.senacor.postbook.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels()


}