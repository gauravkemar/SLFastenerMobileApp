package com.example.slfastenermobileapp.model.login

import com.example.slfastenermobileapp.model.login.MenuAcces
import com.example.slfastenermobileapp.model.login.UserAcces

data class LoginResponse(
    val coordinates: Any,
    val dealerCode: Any,
    val dealerName: Any,
    val email: String,
    val firstName: String,
    val id: Int,
    val isVerified: Boolean,
    val jwtToken: String,
    val lastName: String,
    val locationId: Int,
    val menuAccess: List<MenuAcces>,
    val mobileNumber: String,
    val roleName: String,
    val userAccess: List<UserAcces>,
    val userName: String
)