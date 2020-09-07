package com.senacor.postbook.ui.posts

import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.network.AppApi
import javax.inject.Inject
import com.senacor.postbook.ui.posts.Post as PostUi

class PostsRepository @Inject constructor(
    private val postsDao: PostDao,
    private val appApi: AppApi
) {

    fun getAllPosts(userId: Int) = postsDao.getAllPosts(userId)

    fun getFavoritePosts(userId: Int) = postsDao.getFavoritePosts(userId)

    suspend fun refreshPosts(userId: Int) {
        appApi.getPosts(userId).forEach {
            val postInDb = postsDao.getPost(it.id!!)
            if (postInDb == null)
                postsDao.insertPost(
                    Post(
                        it.userId!!,
                        it.id,
                        it.title ?: "",
                        it.body ?: "",
                        false
                    )
                )
            else
                postsDao.updatePost(
                    Post(
                        it.userId!!,
                        it.id,
                        it.title ?: "",
                        it.body ?: "",
                        postInDb.favorite
                    )
                )
        }
    }

    suspend fun updatePost(userId: Int, post: PostUi) = postsDao.updatePost(Post(userId, post.id, post.title, post.body, post.favorite))
}