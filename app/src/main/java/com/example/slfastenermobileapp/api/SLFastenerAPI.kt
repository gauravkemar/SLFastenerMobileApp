package  com.example.demorfidapp.api





import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.helper.Constants.BARCODE_GENERATE_WITH_PREFIX
import com.example.slfastenermobileapp.helper.Constants.LOGIN_URL
import com.example.slfastenermobileapp.helper.Constants.MERGE_STOCK_ITEMS
import com.example.slfastenermobileapp.helper.Constants.Split_Stock_ITEMS


import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
import com.example.slfastenermobileapp.model.login.LoginRequest
import com.example.slfastenermobileapp.model.login.LoginResponse
import com.example.slfastenermobileapp.model.merge.GetStockItemDetailsOnBarcodeMerge
import com.example.slfastenermobileapp.model.merge.MergeStockLineItemRequest
import com.example.slfastenermobileapp.model.putaway.GetStockItemDetailOnBarcodeResponse
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import com.example.slfastenermobileapp.model.putaway.VerifyLocationOnBarcodeResponse
import com.example.slfastenermobileapp.model.split.GetStockItemDetailsOnBarcodeSplit
import com.example.slfastenermobileapp.model.split.SplitStockItemResquest

import retrofit2.Response
import retrofit2.http.*


interface SLFastenerAPI {


    @POST(LOGIN_URL)
    suspend fun login(
        @Body
        loginRequest: LoginRequest
    ): Response<LoginResponse>


    //DEMO
    @POST(Constants.LOGIN_URL)
    suspend fun putAway(
        @Body
        generalRequst: GeneralRequst
    ): Response<ArrayList<GeneralResponse>>

    @GET(Constants.VERIFY_LOCATION_ON_BARCODE_VALUE)
    suspend fun verifyLoationBarcodeValue(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Query("locBarcodeValue") locBarcodeValue: String?
    ): Response<VerifyLocationOnBarcodeResponse>

    @GET(Constants.GET_STOCK_ITEM_DETAIL_ON_BARCODE)
    suspend fun getStockItemDetailOnBarcode(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Query("barcodeValue") barcodeValue: String?
    ): Response<GetStockItemDetailOnBarcodeResponse>


    @POST(Constants.PROCESS_STOCK_PUT_AWAY_LIST)
    suspend fun processStockPutAwayList(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Body
        processStockPutAwayListRequest: ProcessStockPutAwayListRequest
    ): Response<GeneralResponse>

    @GET(Constants.GET_STOCK_ITEM_DETAIL_ON_BARCODEMERGE)
    suspend fun getStockItemDetailOnBarcodeMerge(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Query("barcodeValue") barcodeValue: String?
    ): Response<GetStockItemDetailsOnBarcodeMerge>

    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeValueWithPrefix
    (
        @Header(Constants.HTTP_HEADER_AUTHORIZATION ) bearerToken: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>

    @POST(MERGE_STOCK_ITEMS)
    suspend fun mergeStockItems
    (
        @Header(Constants.HTTP_HEADER_AUTHORIZATION ) bearerToken: String,
        @Body
        mergeStockLineItemRequest: MergeStockLineItemRequest
    ): Response<GeneralResponse>
    @POST(Split_Stock_ITEMS)
    suspend fun splitStockItems
                (
        @Header(Constants.HTTP_HEADER_AUTHORIZATION ) bearerToken: String,
        @Body
        splitStockLineItemRequest: SplitStockItemResquest
    ): Response<GeneralResponse>
    @GET(Constants.GET_STOCK_ITEM_DETAIL_ON_BARCODESPLIT)
    suspend fun getStockItemDetailOnBarcodeSplit(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) token: String,
        @Query("barcodeValue") barcodeValue: String?
    ): Response<GetStockItemDetailsOnBarcodeSplit>



}