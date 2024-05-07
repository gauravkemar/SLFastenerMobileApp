package com.example.slfastenermobileapp.view

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demorfidapp.helper.Resource
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.adapter.MergeStockItemListAdapter
import com.example.slfastenermobileapp.databinding.ActivityMergeBinding
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.helper.SessionManager
import com.example.slfastenermobileapp.model.merge.MergeStockLineItem
import com.example.slfastenermobileapp.model.merge.MergeStockLineItemRequest
import com.example.slfastenermobileapp.model.putaway.ResponseObjectX
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import com.example.slfastenermobileapp.viewmodel.login.MergeListViewModel
import com.example.slfastenermobileapp.viewmodel.login.MergeListViewModelFactory
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData

import es.dmoral.toasty.Toasty

class MergeActivity : AppCompatActivity(), EMDKManager.EMDKListener, Scanner.StatusListener,
    Scanner.DataListener {
    lateinit var binding: ActivityMergeBinding
    private lateinit var viewModel: MergeListViewModel
    private lateinit var progress: ProgressDialog

    var emdkManager: EMDKManager? = null
    var barcodeManager: BarcodeManager? = null
    var isBarcodeInit = false
    var scanner: Scanner? = null
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    private var token: String? = ""
    private var username: String? = ""
    private var locationCode: String = ""
    lateinit var stockItemDetails: MutableList<ResponseObjectX>
    lateinit var mergeStockLineItem: MutableList<MergeStockLineItem>


    private var mergeStockItemListAdapter: MergeStockItemListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_merge)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")
        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            MergeListViewModelFactory(application, slFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[MergeListViewModel::class.java]
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        username = userDetails[Constants.KEY_USER_NAME].toString()
        stockItemDetails = ArrayList()
        mergeStockLineItem = ArrayList()
        binding.idLayoutHeader.profileTXt.setText(username)
        binding.idLayoutHeader.logouticon.visibility = View.GONE
        viewModel.getStockItemDetailOnBarcodeMutable.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse != null) {
                            try {
                                if (resultResponse.responseObject != null) {
                                    var barcode = resultResponse.responseObject.barcode
                                    if (barcode.isNotEmpty()) {
                                        val existingItem =
                                            stockItemDetails.find { it.barcode == resultResponse.responseObject.barcode }
                                        if (existingItem == null) {
                                            stockItemDetails.add(resultResponse.responseObject)
                                            mergeStockItemListAdapter?.notifyItemInserted(
                                                stockItemDetails.size - 1
                                            )
                                            val stockPutAway = MergeStockLineItem(
                                                resultResponse.responseObject.stockItemId,
                                            )
                                            mergeStockLineItem.add(stockPutAway)
                                        } else {
                                            Toast.makeText(
                                                this@MergeActivity,
                                                "Product Already Exists",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this@MergeActivity,
                                        "${resultResponse.errorMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MergeActivity,
                                    "${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { resultResponse ->
                        Toast.makeText(this, resultResponse, Toast.LENGTH_SHORT).show()
                        session.showToastAndHandleErrors(resultResponse, this@MergeActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        }

        viewModel.getBarcodeValueWithPrefixMutableResponse.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse != null) {
                            try {
                                var barcode=resultResponse.responseMessage
                                callSubmitApi(barcode)

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@MergeActivity,
                                    "${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { resultResponse ->
                        Toast.makeText(this, resultResponse, Toast.LENGTH_SHORT).show()
                        session.showToastAndHandleErrors(resultResponse, this@MergeActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        }


        mergeStockItemListAdapter = MergeStockItemListAdapter(stockItemDetails)
        binding.rcScannedMergeList.adapter = mergeStockItemListAdapter
        binding.btnSubmitFinal.setOnClickListener {
            generateBarcodeForBatches()
        }
        binding.btnSubmit2.setOnClickListener {
            mergeTheList()
        }
        binding.rcScannedMergeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcScannedMergeList.setHasFixedSize(true)
        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
            initBarcode()
        }

    }

    private fun callSubmitApi(barcode: String) {
       try
       {
           viewModel.mergeStockItems(token!!,Constants.BASE_URL,MergeStockLineItemRequest(barcode,mergeStockLineItem))
       }
       catch (e:Exception)
       {
           Toast.makeText(
               this@MergeActivity,
               e.message,
               Toast.LENGTH_LONG
           ).show()
       }
    }

    private fun mergeTheList() {
        if (stockItemDetails.size > 0) {
            val totalStockQty: Double = stockItemDetails.sumByDouble { it.stockQty }
            var tempModel = ResponseObjectX(
                stockItemDetails.get(0).barcode,
                stockItemDetails.get(0).blockedQty,
                stockItemDetails.get(0).description,
                stockItemDetails.get(0).expiryDate,
                stockItemDetails.get(0).grnId,
                stockItemDetails.get(0).internalBatchNo,
                stockItemDetails.get(0).isActive,
                stockItemDetails.get(0).isExpirable,
                stockItemDetails.get(0).isPutAwayDone,
                stockItemDetails.get(0).isQCRequired,
                stockItemDetails.get(0).isSaleBlocked,
                stockItemDetails.get(0).itemCategory,
                stockItemDetails.get(0).itemCode,
                stockItemDetails.get(0).itemGroup,
                stockItemDetails.get(0).itemName,
                stockItemDetails.get(0).itemPrice,
                stockItemDetails.get(0).locationId,
                stockItemDetails.get(0).mhType,
                stockItemDetails.get(0).parentChildLinkedIds,
                stockItemDetails.get(0).qcStatus,
                stockItemDetails.get(0).qtyBlockRemarks,
                stockItemDetails.get(0).qtyBlockedBy,
                stockItemDetails.get(0).qtyForSale,
                stockItemDetails.get(0).qtyReleaseDate,
                stockItemDetails.get(0).saleBlockedBy,
                stockItemDetails.get(0).stockInAt,
                stockItemDetails.get(0).stockItemBatches,
                stockItemDetails.get(0).stockItemId,
                totalStockQty,
                stockItemDetails.get(0).supplierBatchNo,
                stockItemDetails.get(0).uom,
            )
            stockItemDetails.clear()
            stockItemDetails.add(tempModel)
            mergeStockItemListAdapter!!.notifyDataSetChanged()

            binding.btnSubmitFinal.visibility=View.VISIBLE
            binding.btnSubmit2.visibility=View.GONE
        }
    }

    private fun generateBarcodeForBatches() {
        try {
            viewModel.getBarcodeValueWithPrefix(token!!, Constants.BASE_URL, "G")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }

    private fun clearAll() {
        binding.edLocText.setText("")
        locationCode = ""
        stockItemDetails.clear()
        mergeStockLineItem.clear()
        //binding.totalCount.setText("0")
        mergeStockItemListAdapter!!.notifyDataSetChanged()
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }


    //API Call


    private fun getStockItemDetailOnBarcode(s: String) {
        try {
            viewModel.getStockItemDetailOnBarcode(
                token!!,
                Constants.BASE_URL,
                s
            )
        } catch (e: Exception) {
            runOnUiThread { Toasty.warning(this@MergeActivity, e.message.toString()).show() }

        }
    }


    /////barcode code
    private fun initBarcode() {
        isBarcodeInit = true
        Thread.sleep(1000)
        val results = EMDKManager.getEMDKManager(this@MergeActivity, this)
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e("TAG", "EMDKManager object request failed!")
        } else {
            Log.e(
                "TAG",
                "EMDKManager object initialization is   in   progress......."
            )
        }
    }

    override fun onOpened(emdkManager: EMDKManager?) {
        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
            this.emdkManager = emdkManager
            initBarcodeManager()
            initScanner()
        }
    }

    override fun onClosed() {
        if (emdkManager != null) {
            emdkManager!!.release()
            emdkManager = null
        }
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

    override fun onData(scanDataCollection: ScanDataCollection?) {
        var dataStr: String? = ""
        if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
            val scanData = scanDataCollection.scanData
            for (data in scanData) {
                val barcodeData = data.data
                val labelType = data.labelType
                dataStr = barcodeData
            }

            dataStr?.let {
                getStockItemDetailOnBarcode(it)
            }
            Log.e("TAG", "Barcode Data : $dataStr")
        }
    }


    fun initScanner() {
        if (scanner == null) {
            barcodeManager =
                emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
            scanner = barcodeManager!!.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            scanner?.addDataListener(this)
            scanner?.addStatusListener(this)
            scanner?.triggerType = Scanner.TriggerType.HARD
            try {
                scanner?.enable()
            } catch (e: ScannerException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isBarcodeInit) {
            deInitScanner()
        }

    }

    override fun onStatus(statusData: StatusData) {
        val state = statusData.state
        var statusStr = ""
        when (state) {
            StatusData.ScannerStates.IDLE -> {
                statusStr = statusData.friendlyName + " is enabled and idle..."
                setConfig()
                try {
                    scanner!!.read()
                } catch (e: ScannerException) {
                }
            }

            StatusData.ScannerStates.WAITING -> statusStr =
                "Scanner is waiting for trigger press..."

            StatusData.ScannerStates.SCANNING -> statusStr = "Scanning..."
            StatusData.ScannerStates.DISABLED -> {}
            StatusData.ScannerStates.ERROR -> statusStr = "An error has occurred."
            else -> {}
        }
        setStatusText(statusStr)
    }

    private fun setConfig() {
        if (scanner != null) {
            try {
                val config = scanner!!.config
                if (config.isParamSupported("config.scanParams.decodeHapticFeedback")) {
                    config.scanParams.decodeHapticFeedback = true
                }
                scanner!!.config = config
            } catch (e: ScannerException) {
                Log.e("TAG", e.message!!)
            }
        }
    }

    fun setStatusText(msg: String) {
        Log.e("TAG", "StatusText: $msg")
    }

}