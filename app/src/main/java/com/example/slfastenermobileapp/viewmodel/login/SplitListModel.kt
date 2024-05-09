package com.example.slfastenermobileapp.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
import com.example.slfastenermobileapp.model.split.GetStockItemDetailsOnBarcodeSplit
import com.example.slfastenermobileapp.model.split.SplitStockItemResquest

import com.example.slfastenermobileapp.repository.SLFastenerRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class SplitListModel(
application: Application,
private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application){
    //////////////////////////////////////////////////getStockItem
    val getStockItemDetailOnBarcodeMutable: MutableLiveData<Resource<GetStockItemDetailsOnBarcodeSplit>> =
        MutableLiveData()

    fun getStockItemDetailOnBarcode(
        token: String,
        baseUrl: String,
        barcodeValue: String

    ) {
        viewModelScope.launch {
            safeAPICallGetStockItemDetailOnBarcode(token, baseUrl, barcodeValue)
        }
    }

    private suspend fun safeAPICallGetStockItemDetailOnBarcode(
        token: String,
        baseUrl: String,
        barcodeValue: String
    ) {
        getStockItemDetailOnBarcodeMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.getStockItemDetailOnBarcodeSplit(token, baseUrl, barcodeValue)
                getStockItemDetailOnBarcodeMutable.postValue(
                    handleGetStockItemDetailOnBarcodeResponse(response)
                )
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

    private fun handleGetStockItemDetailOnBarcodeResponse(response: Response<GetStockItemDetailsOnBarcodeSplit>): Resource<GetStockItemDetailsOnBarcodeSplit> {
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

    val getBarcodeValueWithPrefixMutableResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun getBarcodeValueWithPrefix(token: String, baseUrl: String, transactionPrefix: String) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefix(token, baseUrl, transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefix(
        token: String,
        baseUrl: String,
        transactionPrefix: String
    ) {
        getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.getBarcodeValueWithPrefix(token, baseUrl, transactionPrefix)
                getBarcodeValueWithPrefixMutableResponse.postValue(
                    handleGetBarcodeValueWithPrefix(
                        response
                    )
                )
            } else {
                getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeValueWithPrefixMutableResponse.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetBarcodeValueWithPrefix(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

    val splitItemsMutableResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun splitStockItems(
        token: String,
        baseUrl: String,
        splitStockItemResquest:SplitStockItemResquest
    ) {
        viewModelScope.launch {
            safeAPICallSplitStockItems(token, baseUrl, splitStockItemResquest)
        }
    }

    private suspend fun safeAPICallSplitStockItems(
        token: String,
        baseUrl: String,
        splitStockItemResquest: SplitStockItemResquest
    ) {
        splitItemsMutableResponse .postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.splitStockItems(token, baseUrl, splitStockItemResquest)
                splitItemsMutableResponse.postValue(handleSplitItems(response))
            } else {
                splitItemsMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> splitItemsMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> splitItemsMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleSplitItems(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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