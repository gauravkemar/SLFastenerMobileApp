package com.example.slfastenermobileapp.repository


import com.example.slfastenermobileapp.api.RetrofitInstance
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.model.login.LoginRequest
import com.example.slfastenermobileapp.model.merge.MergeStockLineItemRequest
import com.example.slfastenermobileapp.model.picklist.FilterPickTransactionRequest
import com.example.slfastenermobileapp.model.picklist.FilterSinglePickRequest
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import com.example.slfastenermobileapp.model.split.SplitStockItemResquest
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



    suspend fun mergeStockItems(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        mergeStockLineItemRequest: MergeStockLineItemRequest
    ) = RetrofitInstance.api(baseUrl).mergeStockItems(bearerToken,mergeStockLineItemRequest)

    suspend fun getStockItemDetailOnBarcodeSplit(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("barcodeValue") barcodeValue: String?
    ) = RetrofitInstance.api(baseUrl).getStockItemDetailOnBarcodeSplit(bearerToken,barcodeValue)

    suspend fun getBarcodeValueWithPrefix(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance.api(baseUrl).getBarcodeValueWithPrefix(bearerToken,transactionPrefix)
    suspend fun getBarcodeValueWithPrefixNew(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance.api(baseUrl).getBarcodeValueWithPrefixNew(bearerToken,transactionPrefix)

    suspend fun splitStockItems(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        splitStockLineItemRequest: SplitStockItemResquest
    ) = RetrofitInstance.api(baseUrl).splitStockItems(bearerToken,splitStockLineItemRequest)

    suspend fun getFilterPickTransaction(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        filterPickTransactionRequest: FilterPickTransactionRequest
    ) = RetrofitInstance.api(baseUrl).getFilterPickTransaction(bearerToken,filterPickTransactionRequest)
    suspend fun getfilterSinglePick(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        filterSinglePickRequst: FilterSinglePickRequest
    ) = RetrofitInstance.api(baseUrl).getfilterSinglePick(bearerToken,filterSinglePickRequst)

}