package com.example.slfastenermobileapp.model.putaway

data class VerifyLocationOnBarcodeResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: Any,
    val responseObject: ResponseObject,
    val statusCode: Int
)