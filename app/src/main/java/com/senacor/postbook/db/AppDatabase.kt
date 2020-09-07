package com.senacor.postbook.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.senacor.postbook.db.model.Comment
import com.senacor.postbook.db.model.CommentDao
import com.senacor.postbook.db.model.Post
import com.senacor.postbook.db.model.PostDao

@Database(entities = [Post::class, Comment::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun postsDao(): PostDao

    abstract fun commentsDao(): CommentDao

}