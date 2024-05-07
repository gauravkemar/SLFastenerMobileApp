package com.example.slfastenermobileapp.repository


import com.example.slfastenermobileapp.api.RetrofitInstance
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.model.login.LoginRequest
import com.example.slfastenermobileapp.model.merge.MergeStockLineItemRequest
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


class SLFastenerRepository {


   /* suspend fun submitRFIDDetails(
        baseUrl: String,
        postRFIDReadRequest: PostRFIDReadRequest
    ) = RetrofitInstance.api(baseUrl).submitRFIDDetails(postRFIDReadRequest)*/
   suspend fun login(
       baseUrl: String,
       loginRequest: LoginRequest
   ) = RetrofitInstance.api(baseUrl).login(loginRequest)

   suspend fun putAway(
       baseUrl: String,
       @Body
       generalRequst: GeneralRequst
   ) = RetrofitInstance.api(baseUrl).putAway(generalRequst)


    suspend fun verifyLoationBarcodeValue(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("locBarcodeValue") locBarcodeValue: String?
    ) = RetrofitInstance.api(baseUrl).verifyLoationBarcodeValue(bearerToken,locBarcodeValue)


    suspend fun getStockItemDetailOnBarcode(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("barcodeValue") barcodeValue: String?
    ) = RetrofitInstance.api(baseUrl).getStockItemDetailOnBarcode(bearerToken,barcodeValue)

    suspend fun getStockItemDetailOnBarcodeMerge(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("barcodeValue") barcodeValue: String?
    ) = RetrofitInstance.api(baseUrl).getStockItemDetailOnBarcodeMerge(bearerToken,barcodeValue)


    suspend fun processStockPutAwayList(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        processStockPutAwayListRequest: ProcessStockPutAwayListRequest
    ) = RetrofitInstance.api(baseUrl).processStockPutAwayList(bearerToken,processStockPutAwayListRequest)

    suspend fun getBarcodeValueWithPrefix(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance.api(baseUrl).getBarcodeValueWithPrefix(bearerToken,transactionPrefix)

    suspend fun mergeStockItems(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        mergeStockLineItemRequest: MergeStockLineItemRequest
    ) = RetrofitInstance.api(baseUrl).mergeStockItems(bearerToken,mergeStockLineItemRequest)

}