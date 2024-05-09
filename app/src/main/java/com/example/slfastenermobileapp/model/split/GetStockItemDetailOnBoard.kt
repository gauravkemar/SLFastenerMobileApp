package com.example.slfastenermobileapp.model.split

import com.example.slfastenermobileapp.model.putaway.ResponseObjectX


data class GetStockItemDetailsOnBarcodeSplit(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: Any,
    val responseObject: ResponseObjectX,
    val statusCode: Int
)