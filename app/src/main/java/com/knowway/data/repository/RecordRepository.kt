package com.knowway.data.repository

import com.knowway.data.model.record.Record
import com.knowway.data.network.RecordApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


class RecordRepository(private val apiService: RecordApiService) {
    fun uploadRecord(file: File, record: Record): Call<String> {
        val requestFile = RequestBody.create("audio/mp4".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", "${record.recordTitle}.mp4", requestFile)

        val recordJson = Gson().toJson(record)
        val recordBody = RequestBody.create("application/json".toMediaTypeOrNull(), recordJson)

        return apiService.uploadRecord(body, recordBody)
    }
}