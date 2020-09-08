package com.senacor.postbook.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.senacor.postbook.R
import com.senacor.postbook.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.login_fragment.*

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userIdInputLayout.error = null
        loginButton.setOnClickListener {
            val userIdText = userIdEditText.text.toString()
            if (userIdText.isEmpty() || !userIdText.isDigitsOnly()) {
                userIdInputLayout.error = "Please provide valid user id"
                return@setOnClickListener
            }
            userIdEditText.hideKeyboard()
            view.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPostsFragment(userIdText.toInt()))
        }
    }
}