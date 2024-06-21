package com.example.slfastenermobileapp.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.example.slfastenermobileapp.helper.Constants.KEY_HTTP
import com.example.slfastenermobileapp.helper.Constants.KEY_ISLOGGEDIN
import com.example.slfastenermobileapp.helper.Constants.KEY_JWT_TOKEN
import com.example.slfastenermobileapp.helper.Constants.KEY_PORT
import com.example.slfastenermobileapp.helper.Constants.KEY_SERVER_IP
import com.example.slfastenermobileapp.helper.Constants.KEY_USER_EMAIL
import com.example.slfastenermobileapp.helper.Constants.KEY_USER_FIRST_NAME
import com.example.slfastenermobileapp.helper.Constants.KEY_USER_LAST_NAME
import com.example.slfastenermobileapp.helper.Constants.KEY_USER_MOBILE_NUMBER
import com.example.slfastenermobileapp.helper.Constants.KEY_USER_NAME
import com.example.slfastenermobileapp.helper.Constants.ROLE_NAME
import com.example.slfastenermobileapp.view.LoginActivity


class SessionManager(context: Context) {
    // Shared Preferences
    var sharedPrefer: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Context
    var context: Context

    // Shared Pref mode
    var PRIVATE_MODE = 0

    // Constructor
    init {
        this.context = context
        sharedPrefer = context.getSharedPreferences(Constants.SHARED_PREF, PRIVATE_MODE)
        editor = sharedPrefer.edit()
    }

    /**
     * Call this method on/after login to store the details in session
     */
    fun createLoginSession(
        firstName: String?,
        lastName: String?,
        email: String?,
        mobileNumber: String?,
        userName: String?,
        jwtToken: String?,
        roleName: String?,

        ) {

        //editor.putString(KEY_USERID, userId)
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_FIRST_NAME, firstName)
        editor.putString(KEY_USER_LAST_NAME, lastName)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_MOBILE_NUMBER, mobileNumber)
        editor.putString(KEY_JWT_TOKEN, jwtToken)
        editor.putString(ROLE_NAME, roleName)

        // commit changes
        editor.commit()
    }




    fun logoutUser() {
        editor.putBoolean(KEY_ISLOGGEDIN, false)
        editor.commit()
    }

    /**
     * Call this method anywhere in the project to Get the stored session data
     */
    /*fun getUserDetails(): HashMap<String, String?> {
        val user = HashMap<String, String?>()
        user["userId"] = sharedPrefer.getString(KEY_USER_ID, null)
        user["userName"] = sharedPrefer.getString(KEY_USER_NAME, null)
        user["rdtId"] = sharedPrefer.getString(KEY_RDT_ID, null)
        user["terminal"] = sharedPrefer.getString(KEY_TERMINAL, null)
        user["jwtToken"] = sharedPrefer.getString(KEY_JWT_TOKEN, null)
        user["refreshToken"] = sharedPrefer.getString(KEY_REFRESH_TOKEN, null)
        return user
    }*/
    /*    fun getUserDetails(): HashMap<String, String?> {
            val user = HashMap<String, String?>()
            user[Constants.SET_BASE_URL] = sharedPrefer.getString(Constants.SET_BASE_URL, null)
            user[Constants.SET_ANTENNA_POWER] = sharedPrefer.getString(Constants.SET_ANTENNA_POWER, null)
            return user
        }*/

    /*  fun getHeaderDetails(): HashMap<String, String?> {
          val user_header = HashMap<String, String?>()
          user_header["UserId"] = sharedPrefer.getString(KEY_USERID, null)
          user_header["RDTId"] = sharedPrefer.getString(KEY_RDT_ID, null)
          user_header["TerminalId"] = sharedPrefer.getString(KEY_TERMINAL, null)
          user_header["Token"] = sharedPrefer.getString(KEY_JWT_TOKEN, null)
          return user_header
      }
  */
    fun isAlreadyLoggedIn(): HashMap<String, Boolean> {
        val user = HashMap<String, Boolean>()
        user["isLoggedIn"] = sharedPrefer.getBoolean(KEY_ISLOGGEDIN, false)
        return user
    }

    fun getAdminDetails(): HashMap<String, String?> {
        val admin = HashMap<String, String?>()
        admin["serverIp"] = sharedPrefer.getString(KEY_SERVER_IP, null)
        admin["port"] = sharedPrefer.getString(KEY_PORT, null)
        return admin
    }

    fun getUserDetails(): HashMap<String, Any?> {
        val user = HashMap<String, Any?>()

        user[KEY_USER_NAME] = sharedPrefer.getString(KEY_USER_NAME, null)
        user[KEY_JWT_TOKEN] = sharedPrefer.getString(KEY_JWT_TOKEN, null)
        user[ROLE_NAME] = sharedPrefer.getString(ROLE_NAME, null)
        user[KEY_USER_FIRST_NAME] = sharedPrefer.getString(KEY_USER_FIRST_NAME, null)
        user[KEY_USER_LAST_NAME] = sharedPrefer.getString(KEY_USER_LAST_NAME, null)
        user[KEY_USER_EMAIL] = sharedPrefer.getString(KEY_USER_EMAIL, null)
        user[KEY_USER_MOBILE_NUMBER] = sharedPrefer.getString(KEY_USER_MOBILE_NUMBER, null)
        user[KEY_SERVER_IP] = sharedPrefer.getString(KEY_SERVER_IP, null)
        user[KEY_HTTP] = sharedPrefer.getString(KEY_HTTP, null)
        user[KEY_PORT] = sharedPrefer.getString(KEY_PORT, null)

        return user
    }

    fun getToken(): String {
        val token = sharedPrefer.getString(KEY_JWT_TOKEN, null)
        return token ?: ""
    }

    fun getRole(): String {
        val role = sharedPrefer.getString(ROLE_NAME, null)
        return role ?: ""
    }


    fun clearSharedPrefs() {
        editor.clear()
        editor.commit()
    }
    fun showToastAndHandleErrors(resultResponse: String,context: Activity) {

        when (resultResponse) {
            "java.net.ConnectException: Failed to connect" -> {
                // Handle connection failure error
                // Show a toast message or display a dialog
            }
            Constants.SESSION_EXPIRE, "Authentication token expired", Constants.CONFIG_ERROR -> {
                showCustomDialog(
                    "Session Expired",
                    "Please re-login to continue",
                    context
                )
            }

        }
    }
    fun showCustomDialog(title: String?, message: String?,context: Activity) {
        var alertDialog: AlertDialog? = null
        val builder: AlertDialog.Builder
        if (title.equals(""))
            builder = AlertDialog.Builder(context)
                .setMessage(Html.fromHtml(message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay") { dialogInterface, which ->
                    alertDialog?.dismiss()
                }
        else if (message.equals(""))
            builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay") { dialogInterface, which ->
                    alertDialog?.dismiss()
                }
        else
            builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay") { dialogInterface, which ->
                    if (title.equals("Session Expired")) {
                        logout(context)
                    } else {
                        alertDialog?.dismiss()
                    }
                }
        alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun logout(context: Activity) {
        logoutUser()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        //context.finish()
        context.finishAfterTransition()
    }
    fun saveAdminDetails(serverIp: String?, http:String?) {
        editor.putString(KEY_SERVER_IP, serverIp)
        editor.putString(KEY_HTTP, http)
        editor.putBoolean(KEY_ISLOGGEDIN, false)
        editor.commit()
    }


}


