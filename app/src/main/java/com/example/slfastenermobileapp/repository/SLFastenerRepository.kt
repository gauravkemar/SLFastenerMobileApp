package com.example.slfastenermobileapp.repository


import com.example.slfastenermobileapp.api.RetrofitInstance
import com.example.slfastenermobileapp.model.login.LoginRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


class SLFastenerRepository {


   /* suspend fun submitRFIDDetails(
        baseUrl: String,
        postRFIDReadRequest: PostRFIDReadRequest
    ) = RetrofitInstance.api(baseUrl).submitRFIDDetails(postRFIDReadRequest)*/
   suspend fun login(
       baseUrl: String,
       loginRequest: LoginRequest
   ) = RetrofitInstance.api(baseUrl).login(loginRequest)



}