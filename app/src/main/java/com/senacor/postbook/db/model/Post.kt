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

    @Query("SELECT * FROM posts ORDER BY id")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE favorite = 'true' ORDER BY id")
    fun getFavoritePosts(): LiveData<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

}