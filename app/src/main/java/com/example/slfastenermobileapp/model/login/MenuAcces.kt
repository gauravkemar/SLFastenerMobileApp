package com.example.slfastenermobileapp.model.login




data class MenuAcces(
    val children: List<Children>,
    val displayName: String,
    val menuIcon: String,
    val parentCategory: Any,
    val parentId: Int,
    val routingURL: String
)