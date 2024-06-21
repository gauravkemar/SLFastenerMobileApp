package com.example.slfastenermobileapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.slfastenermobileapp.repository.SLFastenerRepository

class MergeListViewModelFactory(
    private val application: Application,
    private val rfidRepository: SLFastenerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MergeListViewModel(application, rfidRepository) as T
    }
}