package com.example.slfastenermobileapp.model.picklist

data class FilterSinglePickResponse(
    val assignedAt: Any,
    val assignedUserId: Any,
    val bpId: Any,
    val createdBy: String,
    val createdDate: String,
    val dispatchDate: Any,
    val fromLocId: Int,
    val isActive: Boolean,
    val kPickNo: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val pickLineItemUnit: List<PickLineItemUnit>,
    val pickTranId: Int,
    val remarks: Any,
    val scheduledDispatchDate: Any,
    val sourceModule: String,
    val sourceModuleId: Int,
    val status: String,
    val stockInChargeUserId: Int,
    val toLocId: Int
)