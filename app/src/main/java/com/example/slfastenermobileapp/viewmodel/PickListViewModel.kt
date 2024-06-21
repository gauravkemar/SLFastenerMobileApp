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
import com.example.slfastenermobileapp.model.picklist.FilterPickTransactionRequest
import com.example.slfastenermobileapp.model.picklist.FilterPickTransactionResponse
import com.example.slfastenermobileapp.model.picklist.FilterSinglePickRequest
import com.example.slfastenermobileapp.model.picklist.FilterSinglePickResponse
import com.example.slfastenermobileapp.model.putaway.GetStockItemDetailOnBarcodeResponse
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import com.example.slfastenermobileapp.model.putaway.VerifyLocationOnBarcodeResponse
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class PickListViewModel (
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application) {
    //////////////////////////////////////////////////DEMO
    val filterPickTransactionResponseMutable: MutableLiveData<Resource<ArrayList<FilterPickTransactionResponse>>> = MutableLiveData()
    fun getFilterPickTransaction(
        token:String,
        baseUrl: String,
        filterPickTransactionRequest: FilterPickTransactionRequest
    ) {
        viewModelScope.launch {
            safeAPICallGetFilterPickTransaction(token,baseUrl, filterPickTransactionRequest)
        }
    }

    private suspend fun safeAPICallGetFilterPickTransaction(token:String,baseUrl: String, filterPickTransactionRequest: FilterPickTransactionRequest) {
        filterPickTransactionResponseMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getFilterPickTransaction(token,baseUrl, filterPickTransactionRequest)
                filterPickTransactionResponseMutable.postValue(handleGetFilterPickTransaction(response))
            } else {
                filterPickTransactionResponseMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    filterPickTransactionResponseMutable.postValue(Resource.Error("${t.message}"))
                }

                else -> filterPickTransactionResponseMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleGetFilterPickTransaction(response: Response<ArrayList<FilterPickTransactionResponse>>): Resource<ArrayList<FilterPickTransactionResponse>> {
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


  //////////////////////////////////////////////////DEMO
    val getfilterSinglePickMutable: MutableLiveData<Resource<ArrayList<FilterSinglePickResponse>>> = MutableLiveData()
    fun getfilterSinglePick(
        token:String,
        baseUrl: String,
        filterSinglePickRequest: FilterSinglePickRequest
    ) {
        viewModelScope.launch {
            safeAPICallGetfilterSinglePick(token,baseUrl, filterSinglePickRequest)
        }
    }

    private suspend fun safeAPICallGetfilterSinglePick(token:String,baseUrl: String, filterSinglePickRequest: FilterSinglePickRequest) {
        getfilterSinglePickMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getfilterSinglePick(token,baseUrl, filterSinglePickRequest)
                getfilterSinglePickMutable.postValue(handleGetfilterSinglePick(response))
            } else {
                getfilterSinglePickMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is Exception -> {
                    getfilterSinglePickMutable.postValue(Resource.Error("${t.message}"))
                }

                else -> getfilterSinglePickMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
            }
        }
    }

    private fun handleGetfilterSinglePick(response: Response<ArrayList<FilterSinglePickResponse>>): Resource<ArrayList<FilterSinglePickResponse>> {
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
