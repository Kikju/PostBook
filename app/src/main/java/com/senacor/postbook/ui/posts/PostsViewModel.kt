package com.senacor.postbook.ui.posts

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PostsViewModel @ViewModelInject constructor(
    private val postsRepository: PostsRepository
): ViewModel() {

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean>
        get() = _loading

    val posts = postsRepository.getAllPosts()

    val favoritePosts = postsRepository.getFavoritePosts()

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String?>
        get() = _error

    fun refreshPosts(userId: Int) {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                postsRepository.refreshPosts(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}