package com.example.slfastenermobileapp.model.merge

data class MergeStockLineItemRequest(
    val Barcode: String,
    val MergeStockLineItems: List<MergeStockLineItem>
)