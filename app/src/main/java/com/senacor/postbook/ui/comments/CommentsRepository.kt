package com.senacor.postbook.ui.comments

import androidx.lifecycle.liveData
import com.senacor.postbook.db.model.Comment
import com.senacor.postbook.db.model.CommentDao
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.network.AppApi
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    private val postsDao: PostDao,
    private val commentDao: CommentDao,
    private val appApi: AppApi
) {

    fun getPost(postId: Int) = liveData { emit(postsDao.getPost(postId)!!) }

    fun getComments(postId: Int) = commentDao.getAllComments(postId)

    suspend fun refreshComments(postId: Int) {
        appApi.getComments(postId).forEach {
            commentDao.insertComment(
                Comment(
                    postId,
                    it.id!!,
                    it.name ?: "",
                    it.email ?: "",
                    it.body ?: ""
                )
            )
        }
    }
}