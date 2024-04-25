package com.example.slfastenermobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.slfastenermobileapp.databinding.ActivityHomeMenuBinding
import com.example.slfastenermobileapp.view.PutawayActivity


class HomeMenuActivity : AppCompatActivity() {
    lateinit var binding:ActivityHomeMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_home_menu)
        binding.card1.setOnClickListener {
            startActivity(Intent(this@HomeMenuActivity,PutawayActivity::class.java))
        }
    }
}