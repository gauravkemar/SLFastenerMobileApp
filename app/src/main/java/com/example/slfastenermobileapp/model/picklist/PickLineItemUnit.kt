package com.example.slfastenermobileapp.model.picklist

data class PickLineItemUnit(
    val barcode: String,
    val createdBy: String,
    val createdDate: String,
    val internalBatchNo: Any,
    val isActive: Boolean,
    val lineItemUnitId: Int,
    val locationId: Int,
    val modifiedBy: String,
    val modifiedDate: Any,
    val pickTranId: Int,
    val pickedAt: Any,
    val pickedBy: Any,
    val qty: Double,
    val remarks: Any,
    val sourceLineItemId: Int,
    val stockItemId: Int,
    val supplierBatchNo: String,
    val uoM: String
)