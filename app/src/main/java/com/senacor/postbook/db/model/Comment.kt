package com.senacor.postbook.db.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(entity = Post::class, parentColumns = ["id"], childColumns = ["post_id"])
    ],
    indices = [Index("post_id")]
)
data class Comment(
    @ColumnInfo(name = "post_id") val postId: Int?,
    @PrimaryKey val id: Int?,
    val name: String?,
    val email: String?,
    val body: String?
)

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY id")
    fun getAllPosts(postId: Int): LiveData<List<Comment>>

    @Insert
    suspend fun insertComment(post: Comment)

}