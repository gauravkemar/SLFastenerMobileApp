package com.example.slfastenermobileapp.view

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.viewmodel.login.MergeListViewModel
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerResults

import es.dmoral.toasty.Toasty

class MergeActivity : AppCompatActivity()  {
    private lateinit var viewModel: MergeListViewModel
    private var token: String? = ""
    var emdkManager: EMDKManager? = null
    var barcodeManager: BarcodeManager? = null
    var isBarcodeInit = false
    var scanner: Scanner? = null
    private lateinit var progress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merge)
    }
    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }



    fun initBarcodeManager() {
        barcodeManager =
            emdkManager!!.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
        if (barcodeManager == null) {
            Toast.makeText(
                this@MergeActivity,
                "Barcode scanning is not supported.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    fun deInitScanner() {
        if (scanner != null) {
            try {
                scanner!!.release()
            } catch (e: Exception) {
            }
            scanner = null
        }
    }



    private fun getStockItemDetailOnBarcode(s: String) {
        try {
            viewModel.getStockItemDetailOnBarcodeMerge(
                token!!,
                Constants.BASE_URL,
                s
            )
        } catch (e: Exception) {
            runOnUiThread { Toasty.warning(this@MergeActivity, e.message.toString()).show() }

        }
    }


}



