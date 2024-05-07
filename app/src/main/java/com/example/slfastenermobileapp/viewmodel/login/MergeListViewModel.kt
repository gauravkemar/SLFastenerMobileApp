package com.example.slfastenermobileapp.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
import com.example.slfastenermobileapp.model.merge.GetStockItemDetailsOnBarcodeMerge
import com.example.slfastenermobileapp.model.merge.MergeStockLineItemRequest
import com.example.slfastenermobileapp.model.putaway.GetStockItemDetailOnBarcodeResponse
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class MergeListViewModel (
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application){
    //////////////////////////////////////////////////getStockItem
    val getStockItemDetailOnBarcodeMutable: MutableLiveData<Resource<GetStockItemDetailsOnBarcodeMerge>> = MutableLiveData()
    fun getStockItemDetailOnBarcode(
        token:String,
        baseUrl: String,
        barcodeValue:String

    ) {
        viewModelScope.launch {
            safeAPICallGetStockItemDetailOnBarcode(token,baseUrl, barcodeValue)
        }
    }
    private suspend fun safeAPICallGetStockItemDetailOnBarcode(
        token:String,
        baseUrl: String,
        barcodeValue: String
    ) {
        getStockItemDetailOnBarcodeMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getStockItemDetailOnBarcodeMerge(token,baseUrl,barcodeValue)
                getStockItemDetailOnBarcodeMutable.postValue(handleGetStockItemDetailOnBarcodeResponse(response))
            } else {
                getStockItemDetailOnBarcodeMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getStockItemDetailOnBarcodeMutable.postValue(Resource.Error("${t.message}"))
                }
                else -> getStockItemDetailOnBarcodeMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }
    private fun handleGetStockItemDetailOnBarcodeResponse(response: Response<GetStockItemDetailsOnBarcodeMerge>): Resource<GetStockItemDetailsOnBarcodeMerge> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { Response ->
                return Resource.Success(Response)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val getBarcodeValueWithPrefixMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun getBarcodeValueWithPrefix(token: String,baseUrl: String ,transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefix(token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefix(token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeValueWithPrefix(token,baseUrl,transactionPrefix )
                getBarcodeValueWithPrefixMutableResponse.postValue(handleGetBarcodeValueWithPrefix(response))
            } else {
                getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetBarcodeValueWithPrefix(response: Response<GeneralResponse>): Resource<GeneralResponse>{
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val mergeStockItemsMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun mergeStockItems(token: String,baseUrl: String ,mergeStockLineItemRequest : MergeStockLineItemRequest ) {
        viewModelScope.launch {
            safeAPICallMergeStockItems(token,baseUrl,mergeStockLineItemRequest)
        }
    }

    private suspend fun safeAPICallMergeStockItems(token: String,baseUrl: String,mergeStockLineItemRequest : MergeStockLineItemRequest  ) {
        mergeStockItemsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.mergeStockItems(token,baseUrl,mergeStockLineItemRequest  )
                mergeStockItemsMutableResponse.postValue(handleMergeStockItems(response))
            } else {
                mergeStockItemsMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> mergeStockItemsMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> mergeStockItemsMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleMergeStockItems(response: Response<GeneralResponse>): Resource<GeneralResponse>{
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }
}