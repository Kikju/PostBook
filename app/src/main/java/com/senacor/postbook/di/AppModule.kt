package com.senacor.postbook.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.senacor.postbook.db.AppDatabase
import com.senacor.postbook.db.model.CommentDao
import com.senacor.postbook.db.model.PostDao
import com.senacor.postbook.network.AppApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient): AppApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
        val api = retrofit.create(AppApi::class.java)
        return api
    }

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
        .build()

    @Provides
    @Singleton
    fun providePostsDao(db: AppDatabase): PostDao = db.postsDao()

    @Provides
    @Singleton
    fun provideCommentsDao(db: AppDatabase): CommentDao = db.commentsDao()

}