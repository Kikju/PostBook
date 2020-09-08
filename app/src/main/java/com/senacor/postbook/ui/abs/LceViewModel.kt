package com.senacor.postbook.ui.abs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class LceViewModel: ViewModel() {

    protected val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean>
        get() = _loading

    protected val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String?>
        get() = _error

    protected fun loadContent(load: suspend () -> Unit) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                load()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}