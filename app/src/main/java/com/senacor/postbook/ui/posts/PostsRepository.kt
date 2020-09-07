package com.senacor.postbook.ui.posts

import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.network.AppApi
import javax.inject.Inject

class PostsRepository @Inject constructor(
    private val postsDao: PostDao,
    private val appApi: AppApi
) {

    fun getAllPosts() = postsDao.getAllPosts()

    fun getFavoritePosts() = postsDao.getFavoritePosts()

    suspend fun refreshPosts(userId: Int) {
        appApi.getPosts(userId).forEach {
            postsDao.insertPost(
                Post(
                    it.userId!!,
                    it.id!!,
                    it.title ?: "",
                    it.body ?: "",
                    false
                )
            )
        }
    }
}