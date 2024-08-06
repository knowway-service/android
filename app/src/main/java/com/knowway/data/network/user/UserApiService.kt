package com.knowway.data.network.user

import com.knowway.data.model.user.*
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    @POST("/users")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @POST("/users/emails")
    fun checkEmail(@Body request: EmailDuplicationRequest): Call<Void>

    @POST("/login")
    fun login(@Body request: LoginRequest): Call<Void>

    @POST("/logout")
    fun logout(): Call<Void>

    @POST("/admin")
    fun isUserAdmin(): Call<Void>

    @GET("/users")
    fun getProfile(): Call<UserProfileResponse>

    @GET("/users/chat-id")
    fun getUserChatId(): Call<UserChatMemberIdResponse>


    @GET("/users/records")
    fun getRecord(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("isSelectedByAdmin") isSelectedByAdmin: Boolean?
    ): Call<List<UserRecord>>

    @DELETE("/users/records/{recordId}")
    fun deleteUserRecord(@Path("recordId") recordId: Long): Call<Void>

}
