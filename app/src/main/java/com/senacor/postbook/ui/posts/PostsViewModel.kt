package com.senacor.postbook.ui.posts

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch

private const val USER_ID_SAVED_STATE_KEY = "USER_ID_SAVED_STATE_KEY"
private const val FAVORITE_SAVED_STATE_KEY = "FAVORITE_SAVED_STATE_KEY"

class PostsViewModel @ViewModelInject constructor(
    private val postsRepository: PostsRepository,
    @Assisted private val savedState: SavedStateHandle
): ViewModel() {

    init {
        setFavorite(false)
    }

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean>
        get() = _loading

    val posts = getFavorite().switchMap {
        if (it == true)
            postsRepository.getFavoritePosts(getUserId())
        else
            postsRepository.getAllPosts(getUserId())
    }

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String?>
        get() = _error

    fun setUserId(userId: Int) {
        savedState.set(USER_ID_SAVED_STATE_KEY, userId)
    }

    private fun getUserId() = savedState.get<Int>(USER_ID_SAVED_STATE_KEY)!!

    fun setFavorite(favorite: Boolean) {
        savedState.set(FAVORITE_SAVED_STATE_KEY, favorite)
    }

    private fun getFavorite() = savedState.getLiveData<Boolean>(FAVORITE_SAVED_STATE_KEY)

    fun refreshPosts() {
        viewModelScope.launch {
            _error.value = null
            _loading.value = true
            try {
                postsRepository.refreshPosts(getUserId())
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _loading.value = false
            }
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