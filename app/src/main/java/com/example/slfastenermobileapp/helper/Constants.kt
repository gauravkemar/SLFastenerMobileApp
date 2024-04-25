package com.example.slfastenermobileapp.helper

object Constants {

        const val GET = 1
        const val POST = 2
        const val HTTP_OK = 200
        const val HTTP_CREATED = 201
        const val HTTP_EXCEPTION = 202
        const val HTTP_UPDATED = 204
        const val HTTP_FOUND = 302
        const val HTTP_NOT_FOUND = 404
        const val HTTP_CONFLICT = 409
        const val HTTP_INTERNAL_SERVER_ERROR = 500
        const val HTTP_ERROR = 400
        const val NO_INTERNET = "No Internet Connection"
        const val CONFIG_ERROR = "Please configure network details"
        const val SHARED_PREF = "shared_pref"
        const val LOGGED_IN = "LOGGEDIN"
        const val KEY_ISLOGGEDIN = "isLoggedIn"
        const val HTTP_ERROR_MESSAGE = "message"
        const val KEY_USER_NAME = "userName"
        const val KEY_USER_FIRST_NAME = "firstName"
        const val KEY_USER_LAST_NAME= "lastName"
        const val KEY_USER_EMAIL = "email"
        const val KEY_USER_MOBILE_NUMBER = "mobileNumber"
        const val ROLE_NAME = "roleName"
        const val KEY_JWT_TOKEN = "jwtToken"
        const val KEY_SERVER_IP = "serverIp"
        const val KEY_PORT = "port"
        const val HTTP_HEADER_AUTHORIZATION = "Authorization"
        const val SESSION_EXPIRE = "Session Expired ! Please relogin"


        //const val BASE_URL = "http://103.240.90.141:80/Service/api/"
        //const val BASE_URL = "http://192.168.1.46:5000/api/"
        const val BASE_URL = "http://192.168.1.205:4800/service/api/"


        const val LOGIN_URL = "UserManagement/authenticate"
        const val GET_STOCK_ITEM_DETAIL_ON_BARCODE = "stock/getStockItemDetailOnBarcode"
        const val VERIFY_LOCATION_ON_BARCODE_VALUE = "Location/verifyLocationOnBarcodeValue"
        const val PROCESS_STOCK_PUT_AWAY_LIST = "stock/processStockPutAwayList"



}