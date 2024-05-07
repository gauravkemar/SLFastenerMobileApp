package com.example.slfastenermobileapp.view

import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demorfidapp.helper.Resource
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.adapter.PutAwayStockItemListAdapter
import com.example.slfastenermobileapp.databinding.ActivityPutawayBinding
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.helper.SessionManager
import com.example.slfastenermobileapp.model.putaway.ProcessStockPutAwayListRequest
import com.example.slfastenermobileapp.model.putaway.ResponseObjectX
import com.example.slfastenermobileapp.model.putaway.StockPutAway
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import com.example.slfastenermobileapp.viewmodel.login.PutAwayListViewModel
import com.example.slfastenermobileapp.viewmodel.login.PutAwayListViewModelFactory
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData
import es.dmoral.toasty.Toasty

class PutawayActivity : AppCompatActivity(), EMDKManager.EMDKListener, Scanner.StatusListener,
    Scanner.DataListener {
    lateinit var binding: ActivityPutawayBinding
    private lateinit var viewModel: PutAwayListViewModel
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

    lateinit var putAwayList: MutableList<StockPutAway>
    private var putAwayStockItemListAdapter: PutAwayStockItemListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_putaway)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")
        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            PutAwayListViewModelFactory(application, slFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[PutAwayListViewModel::class.java]
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        username = userDetails[Constants.KEY_USER_NAME].toString()
        stockItemDetails = ArrayList()
        putAwayList = ArrayList()
        binding.idLayoutHeader.profileTXt .setText(username)
        binding.idLayoutHeader.logouticon.visibility= View.GONE

        /*apiCall()
    viewModel.generalMutable.observe(this) { response ->
        when (response) {
            is Resource.Success -> {
                hideProgressBar()

            }
            is Resource.Error -> {
                hideProgressBar()
                response.message?.let { errorMessage ->
                    Toasty.error(
                        this@PutawayActivity,
                        "Login failed - \nError Message: $errorMessage"
                    ).show()
                }
            }
            is Resource.Loading -> {
                showProgressBar()
            }
        }
    }*/

        viewModel.verifyLoationBarcodeValueMutable.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse != null) {
                            try {
                                if(resultResponse.responseObject!=null)
                                {
                                    var locationName = resultResponse.responseObject.locationName
                                    var locCode = resultResponse.responseObject.locationCode
                                    if (locationName.isNotEmpty()) {
                                        binding.edLocText.setText(locationName)
                                    }
                                    if (locCode.isNotEmpty()) {
                                        locationCode = resultResponse.responseObject.locationCode
                                    }
                                }
                                else{
                                    Toast.makeText(
                                        this@PutawayActivity,
                                        "${resultResponse.errorMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }


                            } catch (e: Exception) {
                                Toasty.warning(this@PutawayActivity, e.message.toString()).show()
                            }


                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { resultResponse ->
                        Toast.makeText(this, resultResponse, Toast.LENGTH_SHORT).show()
                        session.showToastAndHandleErrors(resultResponse, this@PutawayActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        }

        viewModel.getStockItemDetailOnBarcodeMutable.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse != null) {
                            try {
                                if(resultResponse.responseObject!=null)
                                {
                                    var barcode = resultResponse.responseObject.barcode
                                    if (barcode.isNotEmpty()) {
                                        val existingItem =
                                            stockItemDetails.find { it.barcode == resultResponse.responseObject.barcode }
                                        if (existingItem == null) {
                                            stockItemDetails.add(resultResponse.responseObject)
                                            binding.totalCount.setText(stockItemDetails.size.toString())
                                            putAwayStockItemListAdapter?.notifyItemInserted(stockItemDetails.size-1)
                                            val stockPutAway = StockPutAway(
                                                barcode = resultResponse.responseObject.barcode,
                                                locationCode = locationCode
                                            )
                                            putAwayList.add(stockPutAway)
                                            /*if (locationCode != "") {

                                            } else {
                                                Toast.makeText(
                                                    this@PutawayActivity,
                                                    "Location is Empty",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }*/

                                        } else {
                                            Toast.makeText(
                                                this@PutawayActivity,
                                                "Product Already Exists",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                }
                                else{
                                    Toast.makeText(
                                        this@PutawayActivity,
                                        "${resultResponse.errorMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }




                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@PutawayActivity,
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
                        session.showToastAndHandleErrors(resultResponse, this@PutawayActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        }
        viewModel.processStockPutAwayListMutable.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    clearAll()
                    response.data?.let { resultResponse ->
                        if (resultResponse != null) {
                            try {
                                var response = resultResponse.responseMessage.toString()
                                if (response.isNotEmpty()) {
                                    Toast.makeText(
                                        this@PutawayActivity,
                                        resultResponse.responseMessage.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else
                                { Toast.makeText(
                                        this@PutawayActivity,
                                        "${resultResponse.errorMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@PutawayActivity,
                                    e.message.toString(),
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
                        session.showToastAndHandleErrors(resultResponse, this@PutawayActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        }

        putAwayStockItemListAdapter = PutAwayStockItemListAdapter(stockItemDetails)
        binding.rcPickList.adapter = putAwayStockItemListAdapter
        binding.rcPickList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcPickList.setHasFixedSize(true)

        binding.btnSubmit.setOnClickListener {
            processStockPutAwayList()
        }

        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
            initBarcode()
        }

    }
    private fun clearAll()
    {
        binding.edLocText.setText("")
        locationCode=""
        stockItemDetails.clear()
        putAwayList.clear()
        binding.totalCount.setText("0")
        putAwayStockItemListAdapter!!.notifyDataSetChanged()
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }
    /*    private fun apiCall()
        {
            try {
                viewModel.putAway(Constants.PUT_AWAY, GeneralRequst("abc"))
            }
            catch (e:Exception)
            {
                Toasty.warning(this@PutawayActivity,"exception: $e").show()
            }
        }*/


    //API Call

    private fun verifyLoationBarcodeValue(s: String) {
        try {
            viewModel.verifyLoationBarcodeValue(
                token!!,
                Constants.BASE_URL,
                s
            )
        } catch (e: Exception) {
            runOnUiThread { Toasty.warning(this@PutawayActivity, e.message.toString()).show() }
        }
    }

    private fun getStockItemDetailOnBarcode(s: String) {
        try {
            viewModel.getStockItemDetailOnBarcode(
                token!!,
                Constants.BASE_URL,
                s
            )
        } catch (e: Exception) {
            runOnUiThread { Toasty.warning(this@PutawayActivity, e.message.toString()).show() }

        }
    }

    private fun processStockPutAwayList() {
        try {
            if (putAwayList.size > 0) {
                if(locationCode!="")
                {
                    updateLocationCode(locationCode)
                    viewModel.processStockPutAwayList(
                        token!!, Constants.BASE_URL,
                        ProcessStockPutAwayListRequest(putAwayList)
                    )
                    Log.d("putawaylist",putAwayList.toString())
                }
                else
                {
                    Toasty.warning(this@PutawayActivity, "No Location Found!!").show()
                }
            } else {
                Toasty.warning(this@PutawayActivity, "No products Found!!").show()
            }

        } catch (e: Exception) {
            Toasty.warning(this@PutawayActivity, e.message.toString()).show()
        }
    }

    /////barcode code
    private fun initBarcode() {
        isBarcodeInit = true
        Thread.sleep(1000)
        val results = EMDKManager.getEMDKManager(this@PutawayActivity, this)
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
                this@PutawayActivity,
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
                //runOnUiThread { binding.edLocText.setText(dataStr) }
                // checkVehicleInsideGeofenceBarcode(dataStr.toString())
                dataStr?.let {
                    if (it.startsWith("Loc")) {
                        verifyLoationBarcodeValue(it)
                    } else {
                        getStockItemDetailOnBarcode(it)
                    }
                }
                Log.e("TAG", "Barcode Data : $dataStr")
            }
        }
    fun updateLocationCode(newLocationCode: String) {
        putAwayList.forEach { item ->
            item.locationCode = newLocationCode
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