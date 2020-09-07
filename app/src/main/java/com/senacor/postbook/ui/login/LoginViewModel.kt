package com.senacor.postbook.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.senacor.postbook.repository.Repository

class LoginViewModel @ViewModelInject constructor(
    repository: Repository
): ViewModel() {

    init {
        println("login view model init")
    }

    override fun onCleared() {
        super.onCleared()
        println("login view model on cleared")
    }
}