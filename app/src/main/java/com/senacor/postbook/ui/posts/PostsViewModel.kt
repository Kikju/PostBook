package com.senacor.postbook.ui.posts

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.senacor.postbook.ui.abs.LceViewModel
import kotlinx.coroutines.launch

private const val USER_ID_SAVED_STATE_KEY = "USER_ID_SAVED_STATE_KEY"
private const val FAVORITE_SAVED_STATE_KEY = "FAVORITE_SAVED_STATE_KEY"

class PostsViewModel @ViewModelInject constructor(
    private val postsRepository: PostsRepository,
    @Assisted private val savedState: SavedStateHandle
): LceViewModel() {

    init {
        setFavorite(false)
        refreshPosts()
    }

    val posts = getFavorite().switchMap {
        if (it == true)
            postsRepository.getFavoritePosts(getUserId())
        else
            postsRepository.getAllPosts(getUserId())
    }

    fun setUserId(userId: Int) {
        savedState.set(USER_ID_SAVED_STATE_KEY, userId)
    }

    private fun getUserId() = savedState.get<Int>(USER_ID_SAVED_STATE_KEY)!!

    fun setFavorite(favorite: Boolean) {
        savedState.set(FAVORITE_SAVED_STATE_KEY, favorite)
    }

    private fun getFavorite() = savedState.getLiveData<Boolean>(FAVORITE_SAVED_STATE_KEY)

    fun refreshPosts() {
        loadContent {
            postsRepository.refreshPosts(getUserId())
        }
    }

    fun updateFavoritePost(post: Post) {
        viewModelScope.launch {
            try {
                postsRepository.updatePost(getUserId(), post)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            }
        }
    }

}