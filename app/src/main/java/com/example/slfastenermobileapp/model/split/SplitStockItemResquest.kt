package com.example.slfastenermobileapp.model.split

data class SplitStockItemResquest(
    val SplitStockLineItems: List<SplitStockLineItem>,
    val StockItemId: Int
)