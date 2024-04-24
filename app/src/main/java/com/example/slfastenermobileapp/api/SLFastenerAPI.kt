package  com.example.demorfidapp.api





import com.example.slfastenermobileapp.helper.Constants.LOGIN_URL
import com.example.slfastenermobileapp.helper.Constants.PUT_AWAY
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
import com.example.slfastenermobileapp.model.login.LoginRequest
import com.example.slfastenermobileapp.model.login.LoginResponse

import retrofit2.Response
import retrofit2.http.*


interface SLFastenerAPI {


    @POST(LOGIN_URL)
    suspend fun login(
        @Body
        loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST(PUT_AWAY)
    suspend fun putAway(
        @Body
        generalRequst: GeneralRequst
    ): Response<ArrayList<GeneralResponse>>




}