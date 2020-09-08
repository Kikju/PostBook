package com.senacor.postbook.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PostDaoTest {


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
    @Throws(Exception::class)
    fun getPost_positive() = runBlockingTest {
        val post = newNonFavoritePost()
        dao.insertPost(post)
        val postFromDb = dao.getPost(1)

        assertThat(postFromDb, equalTo(post))
    }

    @Test
    @Throws(Exception::class)
    fun getPost_negative() = runBlockingTest {
        val post = newNonFavoritePost()
        dao.insertPost(post)
        val postFromDb = dao.getPost(2)

        assertThat(postFromDb, equalTo(null))
    }

}