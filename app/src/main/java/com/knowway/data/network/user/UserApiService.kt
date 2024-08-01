package com.knowway.data.network.user

import com.knowway.data.model.user.*
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    @POST("/users")
    fun registerUser(@Body request: RegisterRequest): Call<Boolean>

    @POST("/users/emails")
    fun checkEmail(@Body request: EmailDuplicationRequest): Call<Boolean>

    @POST("/login")
    fun login(@Body request: LoginRequest): Call<Boolean>

    @POST("/logout")
    fun logout(): Call<Boolean>

    @GET("/users")
    fun getProfile(@Query("email") email: String): Call<UserProfileResponse>

    @GET("/users/userRecords")
    fun getProfile(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("isSelectedByAdmin") isSelectedByAdmin: Boolean
    ): Call<UserRecordResponse>

    @DELETE("/users/record/{recordId}")
    fun deleteUserRecord(@Path("recordId") recordId: Long): Call<Boolean>

}
