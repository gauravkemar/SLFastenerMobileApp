package com.example.slfastenermobileapp.model.generalresponserequest

data class GeneralResponse(
    val errorMessage: String,
    val exception: Any,
    val responseMessage: Any,
    val statusCode: Int
)