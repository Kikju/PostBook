package com.senacor.postbook.db.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(
    tableName = "posts"
)
data class Post(
    @ColumnInfo(name = "user_id") val userId: Int,
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    val favorite: Boolean = false
)

@Dao
interface PostDao {

    @Query("SELECT * FROM posts WHERE user_id = :userId ORDER BY id")
    fun getAllPosts(userId: Int): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE user_id = :userId AND favorite = 1 ORDER BY id")
    fun getFavoritePosts(userId: Int): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPost(id: Int): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePost(post: Post)

}