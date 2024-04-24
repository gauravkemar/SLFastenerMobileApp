package com.example.slfastenermobileapp.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.MainActivity
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import com.example.slfastenermobileapp.viewmodel.LoginViewModel
import com.example.slfastenermobileapp.viewmodel.LoginViewModelFactory


import com.example.slfastenermobileapp.databinding.ActivityLoginBinding
import com.example.slfastenermobileapp.helper.Constants.KEY_ISLOGGEDIN
import com.example.slfastenermobileapp.helper.SessionManager
import com.example.slfastenermobileapp.model.login.LoginRequest

import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var progress: ProgressDialog
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_login)
        session = SessionManager(this)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")

        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = LoginViewModelFactory(application, slFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[LoginViewModel ::class.java]


        if (Utils.getSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, false)) {
            if(Utils.getSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, true)) {
                startActivity(Intent(this@LoginActivity, homeActivity::class.java))
                finish()
            }
        }
        binding.mcvLogin.setOnClickListener {
            login()
        }

        viewModel.loginMutableLiveData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            session.createLoginSession(
                                resultResponse.firstName,
                                resultResponse.lastName,
                                resultResponse.email,
                                resultResponse.mobileNumber.toString(),
                                resultResponse.userName,
                                resultResponse.jwtToken,
                                resultResponse.roleName,
                            )
                            Utils.setSharedPrefsBoolean(this@LoginActivity, KEY_ISLOGGEDIN, true)
                            startActivity()

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@LoginActivity,
                                "hello"+e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@LoginActivity,
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
    fun startActivity()
    {
        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
        finish()
    }
    private fun clear(){
        binding.edittextUsername.setText("")
        binding.editTextPassword.setText("")
    }
    private fun validateInput(userId: String, password: String): String? {
        return when {
            userId.isEmpty() || password.isEmpty() -> "Please enter valid credentials"
            userId.length < 5 -> "Please enter at least 5 characters for the username"
            password.length < 6 -> "Please enter a password with more than 6 characters"
            else -> null
        }
    }
    fun login() {
        try {
            // Fetching user credentials from input fields
            val userId = binding.edittextUsername.text.toString().trim()
            val password = binding.editTextPassword .text.toString().trim()

            // Validate user input
            val validationMessage = validateInput(userId, password)
            if (validationMessage == null) {
                val loginRequest = LoginRequest( password, userId )
                viewModel.login(Constants.BASE_URL, loginRequest)
            } else {
                showErrorMessage(validationMessage)
            }
        } catch (e: Exception) {
            showErrorMessage(e.printStackTrace().toString())
        }
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }
    private fun showErrorMessage(message: String) {
        Toasty.warning(this@LoginActivity, message, Toasty.LENGTH_SHORT).show()
    }
}



/*    private fun login()
{
var username=binding.edittextUsername.text.toString().trim()
var password=binding.editTextPassword.text.toString().trim()

if(username.isNotEmpty() && password.isNotEmpty())
{
    if(username.equals("supervisor") && password.equals("Pass@123"))
    {
        var intent= Intent(this@LoginActivity,HomeActivity::class.java)
        startActivity(intent)
        Utils.setSharedPrefsBoolean(this@LoginActivity, Constants.KEY_ISLOGGEDIN, true)
    }
    else{
        Toast.makeText(this@LoginActivity,"Please enter correct details",Toast.LENGTH_SHORT).show()
    }
}
else
{
    Toast.makeText(this@LoginActivity,"Please fill the details",Toast.LENGTH_SHORT).show()
}
}*/
