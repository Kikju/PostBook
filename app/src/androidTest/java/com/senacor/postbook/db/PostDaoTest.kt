package com.senacor.postbook.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PostDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dao: PostDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        dao = db.postsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    companion object {
        fun newNonFavoritePost() = Post(
            1,
            1,
            "title",
            "body",
            false
        )

        fun newFavoritePost() = Post(
            2,
            2,
            "title",
            "body",
            true
        )
    }

    @Test
    fun getPost_positive() = runBlockingTest {
        val post = newNonFavoritePost()
        dao.insertPost(post)
        val postFromDb = dao.getPost(1)

        assertThat(postFromDb, equalTo(post))
    }

    @Test
    fun getPost_negative() = runBlockingTest {
        val post = newNonFavoritePost()
        dao.insertPost(post)
        val postFromDb = dao.getPost(2)

        assertThat(postFromDb, equalTo(null))
    }

    @Test
    fun getAllPosts() = runBlockingTest {
        dao.insertPost(newNonFavoritePost())
        dao.insertPost(newFavoritePost().copy(userId = 1))
        val posts = dao.getAllPosts(1).getOrAwaitValue()
        assertThat(posts.size, equalTo(2))
        assertThat(posts[0].id, equalTo(1))
        assertThat(posts[1].id, equalTo(2))
    }

    @Test
    fun getFavoritePosts() = runBlockingTest {
        dao.insertPost(newNonFavoritePost())
        dao.insertPost(newFavoritePost())
        assertThat(dao.getFavoritePosts(1).getOrAwaitValue().size, equalTo(0))
        assertThat(dao.getFavoritePosts(2).getOrAwaitValue().size, equalTo(1))
    }

    @Test
    fun updatePosts() = runBlockingTest {
        dao.insertPost(newNonFavoritePost())
        dao.insertPost(newFavoritePost())
        assertThat(dao.getFavoritePosts(1).getOrAwaitValue().size, equalTo(0))
        assertThat(dao.getFavoritePosts(2).getOrAwaitValue().size, equalTo(1))
        val post = dao.getPost(1)!!
        dao.updatePost(post.copy(favorite = true))
        assertThat(dao.getFavoritePosts(1).getOrAwaitValue().size, equalTo(1))
        assertThat(dao.getFavoritePosts(2).getOrAwaitValue().size, equalTo(1))
    }

}