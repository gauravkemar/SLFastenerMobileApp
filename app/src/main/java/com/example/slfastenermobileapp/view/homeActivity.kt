package com.example.slfastenermobileapp.view

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.helper.Resource
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.databinding.ActivityPutawayBinding
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.helper.SessionManager
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralRequst
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import com.example.slfastenermobileapp.viewmodel.login.PutAwayListViewModel
import com.example.slfastenermobileapp.viewmodel.login.PutAwayListViewModelFactory
import es.dmoral.toasty.Toasty

class homeActivity : AppCompatActivity() {
    lateinit var binding: ActivityPutawayBinding
    private lateinit var viewModel: PutAwayListViewModel
    private lateinit var progress: ProgressDialog
    private lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_home_menu)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")
        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = PutAwayListViewModelFactory(application, slFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[PutAwayListViewModel ::class.java]
       // apiCall()
        viewModel.generalMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()

                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@homeActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

    }
    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }
//    private fun apiCall()
//    {
//        try {
//            viewModel.putAway(Constants.PUT_AWAY, GeneralRequst("abc"))
//        }
//        catch (e:Exception)
//        {
//            Toasty.warning(this@homeActivity,"exception: $e").show()
//        }
//    }
}