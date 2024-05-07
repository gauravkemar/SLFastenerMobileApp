package com.example.slfastenermobileapp.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.putaway.GetStockItemDetailOnBarcodeResponse
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MergeListViewModel (
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application){
    val getStockItemDetailOnBarcodeMutable: MutableLiveData<Resource<GetStockItemDetailOnBarcodeResponse>> = MutableLiveData()
    fun getStockItemDetailOnBarcodeMerge(
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

}