package com.senacor.postbook.ui.comments

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.senacor.postbook.ui.abs.LceViewModel

private const val POST_ID_SAVED_STATE_KEY = "POST_ID_SAVED_STATE_KEY"

class CommentsViewModel @ViewModelInject constructor(
    private val commentsRepository: CommentsRepository,
    @Assisted private val savedState: SavedStateHandle
): LceViewModel() {

    val post = getPostIdLiveData().switchMap {
        commentsRepository.getPost(it)
    }

    val comments = getPostIdLiveData().switchMap {
        commentsRepository.getComments(it)
    }

    fun setPostId(postId: Int) {
        savedState.set(POST_ID_SAVED_STATE_KEY, postId)
    }

    private fun getPostIdLiveData() = savedState.getLiveData<Int>(POST_ID_SAVED_STATE_KEY)

    private fun getPostId() = savedState.get<Int>(POST_ID_SAVED_STATE_KEY)!!

    fun refreshComments() {
        loadContent {
            commentsRepository.refreshComments(getPostId())
        }
    }
}