package com.example.slfastenermobileapp.model.putaway

data class ResponseObject(
    val createdBy: String,
    val createdDate: String,
    val displayName: String,
    val isActive: Boolean,
    val locationCode: String,
    val locationId: Int,
    val locationName: String,
    val locationType: String,
    val modifiedBy: String,
    val modifiedDate: String,
    val parentLocationCode: String,
    val remarks: String,
    val storageCapacity: Int
)