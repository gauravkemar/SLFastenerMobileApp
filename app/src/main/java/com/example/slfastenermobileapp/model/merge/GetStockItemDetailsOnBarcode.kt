package com.example.slfastenermobileapp.model.merge

import com.example.slfastenermobileapp.model.putaway.ResponseObjectX

data class GetStockItemDetailsOnBarcodeMerge(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: Any,
    val responseObject: ResponseObjectX,
    val statusCode: Int
)