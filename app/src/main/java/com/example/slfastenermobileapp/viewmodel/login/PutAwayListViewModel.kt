package com.example.slfastenermobileapp.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
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
}
