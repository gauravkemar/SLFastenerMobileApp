package com.example.slfastenermobileapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
import com.example.slfastenermobileapp.model.putaway.GetStockItemDetailOnBarcodeResponse
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import com.example.slfastenermobileapp.model.putaway.VerifyLocationOnBarcodeResponse
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class PutAwayListViewModel(
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application) {
    //////////////////////////////////////////////////DEMO
    val generalMutable: MutableLiveData<Resource<ArrayList<GeneralResponse>>> = MutableLiveData()
    fun putAway(
        baseUrl: String,
        generalRequst: GeneralRequst
    ) {
        viewModelScope.launch {
            safeAPICallPutAway(baseUrl, generalRequst)
        }
    }

    private suspend fun safeAPICallPutAway(baseUrl: String,  generalRequst: GeneralRequst) {
        generalMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.putAway(baseUrl, generalRequst)
                generalMutable.postValue(handlePutAwayResponse(response))
            } else {
                generalMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    generalMutable.postValue(Resource.Error("${t.message}"))
                }
                else -> generalMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handlePutAwayResponse(response: Response<ArrayList<GeneralResponse>>): Resource<ArrayList<GeneralResponse>> {
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



    //////////////////////////////////////////////////verifyLoation
    val verifyLoationBarcodeValueMutable: MutableLiveData<Resource<VerifyLocationOnBarcodeResponse>> = MutableLiveData()
    fun verifyLoationBarcodeValue(
        token:String,
        baseUrl: String,
        locBarcodeValue:String

    ) {
        viewModelScope.launch {
            safeAPICallVerifyLoationBarcodeValue(token,baseUrl, locBarcodeValue)
        }
    }

    private suspend fun safeAPICallVerifyLoationBarcodeValue(
        token:String,
        baseUrl: String,
        locBarcodeValue: String
    ) {
        verifyLoationBarcodeValueMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.verifyLoationBarcodeValue(token,baseUrl,locBarcodeValue)
                verifyLoationBarcodeValueMutable.postValue(handleVerifyLoationBarcodeValueResponse(response))
            } else {
                verifyLoationBarcodeValueMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    verifyLoationBarcodeValueMutable.postValue(Resource.Error("${t.message}"))
                }
                else -> verifyLoationBarcodeValueMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleVerifyLoationBarcodeValueResponse(response: Response<VerifyLocationOnBarcodeResponse>): Resource<VerifyLocationOnBarcodeResponse> {
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


    //////////////////////////////////////////////////getStockItem
    val getStockItemDetailOnBarcodeMutable: MutableLiveData<Resource<GetStockItemDetailOnBarcodeResponse>> = MutableLiveData()
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
                val response = rfidRepository.getStockItemDetailOnBarcode(token,baseUrl,barcodeValue)
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
    private fun handleGetStockItemDetailOnBarcodeResponse(response: Response<GetStockItemDetailOnBarcodeResponse>): Resource<GetStockItemDetailOnBarcodeResponse> {
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


  //////////////////////////////////////////////////getStockItem
    val processStockPutAwayListMutable: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    fun processStockPutAwayList(
        token:String,
        baseUrl: String,
        processStockPutAwayListRequest: ProcessStockPutAwayListRequest

    ) {
        viewModelScope.launch {
            safeAPICallProcessStockPutAwayList(token,baseUrl, processStockPutAwayListRequest)
        }
    }

    private suspend fun safeAPICallProcessStockPutAwayList(
        token:String,
        baseUrl: String,
        processStockPutAwayListRequest: ProcessStockPutAwayListRequest
    ) {
        processStockPutAwayListMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processStockPutAwayList(token,baseUrl,processStockPutAwayListRequest)
                processStockPutAwayListMutable.postValue(handleProcessStockPutAwayListResponse(response))
            } else {
                processStockPutAwayListMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    processStockPutAwayListMutable.postValue(Resource.Error("${t.message}"))
                }
                else -> processStockPutAwayListMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }
    private fun handleProcessStockPutAwayListResponse(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

}
