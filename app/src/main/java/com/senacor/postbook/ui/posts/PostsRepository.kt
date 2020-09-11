package com.senacor.postbook.ui.posts

import androidx.lifecycle.LiveData
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.network.AppApi
import javax.inject.Inject
import com.senacor.postbook.ui.posts.Post as PostUi

interface PostsRepository {
    fun getAllPosts(userId: Int): LiveData<List<Post>>
    fun getFavoritePosts(userId: Int): LiveData<List<Post>>

    suspend fun refreshPosts(userId: Int)
    suspend fun updatePost(userId: Int, post: PostUi)
}

class PostsRepositoryImpl @Inject constructor(
    private val postsDao: PostDao,
    private val appApi: AppApi
): PostsRepository {

    override fun getAllPosts(userId: Int) = postsDao.getAllPosts(userId)

    override fun getFavoritePosts(userId: Int) = postsDao.getFavoritePosts(userId)

    override suspend fun refreshPosts(userId: Int) {
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

    override suspend fun updatePost(userId: Int, post: PostUi) = postsDao.updatePost(Post(userId, post.id, post.title, post.body, post.favorite))
}