package com.example.slfastenermobileapp.model.generalresponserequest

data class GeneralResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val statusCode: Int
)