package com.senacor.postbook.ui.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.senacor.postbook.db.model.Comment
import com.senacor.postbook.db.model.CommentDao
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.network.AppApi
import javax.inject.Inject

interface CommentsRepository {
    fun getPost(postId: Int): LiveData<Post>
    fun getComments(postId: Int): LiveData<List<Comment>>
    suspend fun refreshComments(postId: Int)
}

class CommentsRepositoryImpl @Inject constructor(
    private val postsDao: PostDao,
    private val commentDao: CommentDao,
    private val appApi: AppApi
): CommentsRepository {

    override fun getPost(postId: Int) = liveData { emit(postsDao.getPost(postId)!!) }

    override fun getComments(postId: Int) = commentDao.getAllComments(postId)

    override suspend fun refreshComments(postId: Int) {
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