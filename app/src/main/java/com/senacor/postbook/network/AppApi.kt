package com.senacor.postbook.network

import com.senacor.postbook.network.model.Comment
import com.senacor.postbook.network.model.Post
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface AppApi {

    @GET("/posts")
    suspend fun getPosts(
        @Query("userId") userId: Int? = null
    ): List<Post>

    @GET("/posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: Int? = null
    ): List<Comment>

}