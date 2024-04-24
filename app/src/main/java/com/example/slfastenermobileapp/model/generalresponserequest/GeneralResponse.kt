package com.example.slfastenermobileapp.model.generalresponserequest

data class GeneralResponse(
    val errorMessage: String,
    val exception: String,
    val responseMessage: String,
    val statusCode: String
)