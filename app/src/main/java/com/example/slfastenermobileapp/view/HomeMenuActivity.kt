package com.example.slfastenermobileapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.databinding.ActionBarHeaderLayoutBinding
import com.example.slfastenermobileapp.databinding.ActivityHomeMenuBinding
import com.example.slfastenermobileapp.databinding.ActivityPutawayBinding
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.helper.SessionManager


class HomeMenuActivity : AppCompatActivity() {
    lateinit var binding:ActivityHomeMenuBinding

    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    private var token: String? = ""
    private var username: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_home_menu)

        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        username = userDetails[Constants.KEY_USER_NAME].toString()
        binding.idLayoutHeader
            .profileTXt.setText(username)


        binding.card1.setOnClickListener {
            startActivity(Intent(this@HomeMenuActivity,PutawayActivity::class.java))
        }
        binding.card2.setOnClickListener { startActivity(Intent(this@HomeMenuActivity,MergeActivity::class.java ))}

        binding.card3.setOnClickListener { startActivity(Intent(this@HomeMenuActivity,SplitingActivity::class.java)) }
        binding.card5.setOnClickListener { startActivity(Intent(this@HomeMenuActivity,ExitclearanceAcitvity::class.java)) }
        binding.idLayoutHeader.logouticon.setOnClickListener {
            showLogoutDialog()

        }
    }
    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                logout()
            }
            .setNegativeButton("Cancel") { dialog, which ->

                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }
    private fun logout(){
        session.logoutUser()
        startActivity(Intent(this@HomeMenuActivity, LoginActivity::class.java))
        finish()
    }
}