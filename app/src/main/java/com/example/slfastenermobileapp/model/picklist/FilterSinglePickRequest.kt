package com.example.slfastenermobileapp.model.picklist

data class FilterSinglePickRequest(
    val ActualDispatchDate: String,
    val AssignedUserId: String,
    val Barcode: String,
    val IncludeChildren: String,
    val KPickNo: String,
    val LineItemUnitId: String,
    val PickTranId: String,
    val PlannedDispatchDate: String,
    val SourceModule: String,
    val SourceModuleId: Int,
    val Status: String,
    val StockInChargeUserId: String,
    val StockItemId: Int
)