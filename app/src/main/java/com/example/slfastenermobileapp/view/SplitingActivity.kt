package com.example.slfastenermobileapp.view

import android.app.ProgressDialog
import android.content.Intent
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
import com.example.slfastenermobileapp.adapter.SplitStockItemListAdapter
import com.example.slfastenermobileapp.databinding.ActivitySplitingBinding
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.helper.SessionManager
import com.example.slfastenermobileapp.model.split.CustomSplitModel
import com.example.slfastenermobileapp.model.split.GetStockItemDetailsOnBarcodeSplit
import com.example.slfastenermobileapp.model.split.SplitStockItemResquest
import com.example.slfastenermobileapp.model.split.SplitStockLineItem
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import com.example.slfastenermobileapp.viewmodel.SplitListModel
import com.example.slfastenermobileapp.viewmodel.SplitListModelFactory
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData
import es.dmoral.toasty.Toasty


class SplitingActivity : AppCompatActivity(), EMDKManager.EMDKListener, Scanner.StatusListener,
    Scanner.DataListener {
    lateinit var binding: ActivitySplitingBinding
    private lateinit var viewModel: SplitListModel
    private lateinit var progress: ProgressDialog

    var emdkManager: EMDKManager? = null
    var barcodeManager: BarcodeManager? = null
    var isBarcodeInit = false
    var scanner: Scanner? = null
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    private var token: String? = ""
    private var username: String? = ""
    lateinit var getStockItemDetailsOnBarcodeSplit: GetStockItemDetailsOnBarcodeSplit

    private var splitStockItemListAdapter: SplitStockItemListAdapter? = null
    lateinit var stockItemDetails: MutableList<CustomSplitModel>
    lateinit var originalList: MutableList<CustomSplitModel>
    lateinit var splitItemRequest: MutableList<SplitStockItemResquest>

    var balQty = 0.00
    var totalQty = 0.00

    private var stockItemId: String? = ""
    private var currentSelectedItem: CustomSplitModel? = null
    private var serverIpSharedPrefText: String=""
    private var serverHttpPrefText: String= ""
    private var baseUrl: String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_spliting)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")
        val drawable = ContextCompat.getDrawable(this, R.drawable.back_arrow)
        binding.idLayoutHeader.ivBackOrLogo.setImageDrawable(drawable)
        binding.idLayoutHeader.ivBackOrLogo.setOnClickListener {
            onBackPressed()
        }
        binding.idLayoutHeader.titleText.setText("Split Item")
        binding.idLayoutHeader.titleText.visibility = View.VISIBLE
        binding.idLayoutHeader.profileTXt.visibility = View.GONE
        binding.idLayoutHeader.logouticon.visibility = View.GONE
        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            SplitListModelFactory(application, slFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[SplitListModel::class.java]
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails[Constants.KEY_JWT_TOKEN].toString()
        username = userDetails[Constants.KEY_USER_NAME].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"


        binding.clProdItemDetails.visibility = View.GONE
        binding.clBottom.visibility = View.GONE
        stockItemDetails = ArrayList()
        originalList = ArrayList()
        splitItemRequest = ArrayList()
        viewModel.getStockItemDetailOnBarcodeMutable.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        if (resultResponse != null) {
                            try {
                                binding.clChild1.visibility = View.GONE
                                binding.clChild2.visibility = View.VISIBLE
                                binding.clProdItemDetails.visibility = View.VISIBLE
                                binding.mcvSplit.visibility = View.VISIBLE
                                binding.clBottom.visibility = View.VISIBLE
                                binding.mvTittle.setBackgroundResource(R.drawable.oneside_round)
                                getStockItemDetailsOnBarcodeSplit = resultResponse
                                stockItemId = resultResponse.responseObject.stockItemId.toString()
                                setProductItemDetails()
                                if (stockItemDetails.size == 0) {
                                    generateBarcodeForExisitngItem()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@SplitingActivity,
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
                        session.showToastAndHandleErrors(resultResponse, this@SplitingActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                else -> {}
            }
        }
        splitStockItemListAdapter =
            SplitStockItemListAdapter(
                stockItemDetails,
                addItem = { newItem ->
                    currentSelectedItem = newItem
                    getStockItemDetailOnBarcodeSplitNew()
                },
                onSave = { position, updatedItem ->
                    if (updatedItem.Qty == "") {
                        Toast.makeText(
                            this@SplitingActivity,
                            "Please fill the Qty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        calculateQty( position, updatedItem)
                    }
                },
                onDelete = { position,stockItem ->

                    stockItemDetails.removeAt(position)
                    splitStockItemListAdapter!!.notifyItemRemoved(position)
                    splitStockItemListAdapter!!.notifyItemRangeChanged(position,stockItemDetails.size)
                    val itemPosition = stockItemDetails.indexOfFirst { it.Barcode == stockItem.Barcode }
                    val originalListItemPosition = originalList.indexOfFirst { it.Barcode == stockItem.Barcode }
                    originalList!!.removeAt(originalListItemPosition)
                    //Log.e("recyclerItemPosition",itemPosition.toString()+"${originalListItemPosition.toString() +"//"+  originalList.toString()}")
                    var totalReceivedTotalFromList = 0.00
                    if (stockItemDetails.size>0) {
                        totalReceivedTotalFromList = stockItemDetails.sumByDouble { it.Qty.toDouble()}
                            val balQtyFormat = String.format("%.2f", totalReceivedTotalFromList)
                            balQty=totalQty-balQtyFormat.toDouble()
                            binding.tvBalQtyValue.setText(balQty.toString())
                    }
                    else
                    {
                        totalReceivedTotalFromList = totalQty
                        val balQtyFormat = String.format("%.2f", totalReceivedTotalFromList)
                        balQty=balQtyFormat.toDouble()
                        binding.tvBalQtyValue.setText(balQty.toString())
                    }
                },
            )
        binding.rcSplitlist.adapter = splitStockItemListAdapter
        binding.rcSplitlist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mcvSplit.setOnClickListener {
            generateBarcodeForExisitngItem()
        }
        viewModel.getBarcodeValueWithPrefixNewMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            val tempList = stockItemDetails.toMutableList()
                            currentSelectedItem?.let { tempList.add(it) }
                            val newBarcode = resultResponse.responseMessage.toString()
                            val hasItemWithZeroReceivedQuantity = stockItemDetails?.any {
                                it.Qty == "0.00" || it.Qty == "0" || it.Qty == "" || it.Qty=="0.0"
                            } ?: false

                            val sumOfReceivedQtyIncludingUpdatedItem =
                                tempList.sumByDouble { it.Qty.toDouble() }
                            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                                Toast.makeText(
                                    this,
                                    "Value must not exceed the Balance Qty.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (hasItemWithZeroReceivedQuantity) {
                                    Toasty.warning(
                                        this@SplitingActivity,
                                        "Please complete current transaction!!"
                                    )
                                        .show()
                                    Log.d("edBatchNo.isNotEmpty()", "sad")
                                } else {
                                    val barcodeExists =
                                        stockItemDetails!!.any { it.Barcode == newBarcode }
                                    if (!barcodeExists) {

                                        stockItemDetails.add(
                                            createSingleExistingItem(
                                                newBarcode,
                                                currentSelectedItem!!
                                            )
                                        )
                                        originalList.add(createSingleExistingItem(
                                            newBarcode,
                                            currentSelectedItem!!
                                        ))
                                        val sumOfReceivedQtyIncludingUpdatedItem = stockItemDetails.sumByDouble { it.Qty.toDouble() }
                                        Log.e("sumOfReceivedQtyIncludingUpdatedItem",sumOfReceivedQtyIncludingUpdatedItem.toString())
                                        balQty=totalQty-sumOfReceivedQtyIncludingUpdatedItem
                                        Log.e("sumOfReceivedQtyIncludingUpdatedItemtotalQty",balQty.toString())
                                        Log.e("sumOfReceivedQtyIncludingUpdatedItemstockItemDetails",stockItemDetails.toString())
                                        binding.tvBalQtyValue.setText(balQty.toString())
                                        splitStockItemListAdapter?.notifyItemInserted(
                                            stockItemDetails.size - 1
                                        )
                                    } else {

                                    }
                                }
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@SplitingActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@SplitingActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        viewModel.getBarcodeValueWithPrefixMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            addBatchNewItem(resultResponse.responseMessage.toString())
                        } catch (e: Exception) {
                            Log.e("stock1", "7")
                            Toasty.warning(
                                this@SplitingActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@SplitingActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        //need to change the icons for print
        viewModel.splitItemsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if(resultResponse.responseMessage!=null)
                            {
                                Toasty.success(
                                    this@SplitingActivity,
                                    resultResponse.responseMessage.toString(),
                                    Toasty.LENGTH_SHORT
                                ).show()

                            }
                            sentToHome()

                        } catch (e: Exception) {
                            Log.e("stock1", "7")
                            Toasty.warning(
                                this@SplitingActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@SplitingActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        binding.btnSubmit.setOnClickListener {
            callSubmitApi()
        }
        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
            initBarcode()
        }
    }

    private fun sentToHome() {
        startActivity(Intent(this@SplitingActivity,HomeMenuActivity::class.java))
        finish()
    }

    private fun addBatchNewItem(data: String) {
        val hasItemWithZeroReceivedQuantity = stockItemDetails?.any {
            it.Qty == "0.00" || it.Qty == "0" || it.Qty == "" || it.Qty=="0.0"
        } ?: false

        if (hasItemWithZeroReceivedQuantity) {
            Toasty.warning(this@SplitingActivity, "Please complete current transaction!!")
                .show()
            Log.d("edBatchNo.isNotEmpty()", "sad")
        }
        else {
            if (stockItemDetails.size > 0) {
                val total = stockItemDetails?.sumByDouble {
                    it.Qty.toDouble()
                }
                if (total != null) {
                    if (total > balQty) {
                        Toasty.warning(
                            this@SplitingActivity,
                            "Please complete current transaction!!"
                        ).show()
                        Log.d("edBatchNo.isNotEmpty()", "sad")

                    } else {

                        originalList.add(createSingleItem(data))
                        stockItemDetails.add(createSingleItem(data))
                        splitStockItemListAdapter?.notifyItemInserted(stockItemDetails.size - 1)
                    }
                }
            } else {
                stockItemDetails.add(createSingleItem(data))
                originalList.add(createSingleItem(data))
                splitStockItemListAdapter?.notifyItemInserted(stockItemDetails.size - 1)
            }

        }
    }
    private fun createSingleItem(
        newBarcode: String
    ): CustomSplitModel {
        return CustomSplitModel(
            newBarcode,
            "0.00",
            false
        )
    }
    private fun createSingleExistingItem(
        newBarcode: String,
        customSplitModel: CustomSplitModel,

        ): CustomSplitModel {
        return CustomSplitModel(
            newBarcode,
            customSplitModel.Qty,
            false
        )
    }

    /* private fun calculateQty(tempList: MutableList<CustomSplitModel>, position: Int, updatedItem: CustomSplitModel)
     {

         if ( updatedItem.Qty != "0.000") {
             val sumOfReceivedQtyIncludingUpdatedItem =
                 tempList.sumByDouble { it.Qty.toDouble()
                 }
             if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble())
             {
                 Toast.makeText(
                     this,
                     "Value must not exceed the Balance Qty.",
                     Toast.LENGTH_SHORT
                 ).show()
             }
             else {
                 var balQty=stockItemDetails.sumByDouble { it.Qty.toDouble() }
                 var balQ=totalQty-balQty
                 binding.tvBalQtyValue.setText(balQ.toString())
                 stockItemDetails[position] = updatedItem.copy()
                 splitStockItemListAdapter!!.notifyItemChanged(position)
             }
         }
         else
         {
             Toast.makeText(
                 this,
                 "Value must not be 0.",
                 Toast.LENGTH_SHORT
             ).show()
         }


     }*/

    private fun calculateQty(
        position: Int,
        updatedItem: CustomSplitModel
    ) {
        if (updatedItem.Qty != "0.00" && updatedItem.Qty != "" && updatedItem.Qty != "0" && updatedItem.Qty != "0.0") {

            val sumOfReceivedQtyIncludingUpdatedItem =
                stockItemDetails.sumByDouble { it.Qty.toDouble() }
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            }else
            {
                Log.e("recyclerItemPositionFromSave",position.toString())
                val previousReceivedQty = originalList[position].Qty.toDouble()
                val updatedReceivedQty = updatedItem.Qty.toDouble()
                if (updatedReceivedQty != previousReceivedQty)
                { // Only proceed if received quantity has changed
                    val receivedQtyDifference = updatedReceivedQty - previousReceivedQty
                    if (receivedQtyDifference > 0) { // Received quantity increased
                        if (receivedQtyDifference > balQty.toDouble()) {
                            // Show error message, quantity must not exceed the balance quantity
                        }
                        else {
                            // Subtract the difference from the balance quantity
                            val newBalanceQty = balQty.toDouble() - receivedQtyDifference
                            val balQtyFormat = String.format("%.2f", newBalanceQty)

                            stockItemDetails[position].Qty = updatedItem.Qty
                            originalList[position].Qty = updatedItem.Qty
                            balQty = balQtyFormat.toDouble()
                            binding.tvBalQtyValue.setText(balQty.toString())
                            splitStockItemListAdapter!!.notifyItemChanged(position)

                        }
                    }
                    else
                    { // Received quantity decreased
                        // Add the difference to the balance quantity
                        val newBalanceQty = balQty.toDouble() + (-receivedQtyDifference)
                        val balQtyFormat = String.format("%.2f", newBalanceQty)
                        balQty = balQtyFormat.toDouble()
                        stockItemDetails[position].Qty = updatedItem.Qty
                        originalList[position].Qty = updatedItem.Qty
                        binding.tvBalQtyValue.setText(balQty.toString())
                        splitStockItemListAdapter!!.notifyItemChanged(position)

                    }
                }

            }

        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
/*
    private fun calculateQty(
        position: Int,
        updatedItem: CustomSplitModel
    ) {

        if (updatedItem.Qty != "0.00" && updatedItem.Qty != "" && updatedItem.Qty != "0" && updatedItem.Qty!="0.0") {
            val previousQty = stockItemDetails[position].originalQty.toDouble()
            val updatedQty = updatedItem.Qty.toDouble()
            if (updatedQty != previousQty) {
                val qtyDifference = updatedQty - previousQty
                val totalQtySum = stockItemDetails.sumByDouble { it.Qty.toDouble() }
                if (qtyDifference > 0) { // Quantity increased
                    if (totalQtySum > totalQty.toDouble()) {
                        Toast.makeText(
                            this,
                            "Value must not exceed the Balance Qty.",
                            Toast.LENGTH_SHORT
                        ).show()
                        stockItemDetails[position].Qty="0.00"
                        val newBalanceQty = totalQty.toDouble() - (-qtyDifference)
                        val balQtyFormat = String.format("%.2f", newBalanceQty)
                        binding.tvBalQtyValue.setText(balQtyFormat)
                        // Notify adapter about the item change
                        splitStockItemListAdapter!!.notifyItemChanged(position)
                        Log.e("QTY FROM >0 and >total if",stockItemDetails.toString() )

                    } else {
                        // Update stockItemDetails with the new quantity
                        Log.e("QTY FROM >0 < then total else",stockItemDetails.toString() )
                        stockItemDetails[position] = updatedItem.copy()
                        val newBalanceQty = totalQty.toDouble() - qtyDifference
                        val balQtyFormat = String.format("%.2f", newBalanceQty)
                        binding.tvBalQtyValue.setText(balQtyFormat)
                        stockItemDetails[position].originalQty=updatedItem.Qty
                        // Notify adapter about the item change
                        splitStockItemListAdapter!!.notifyItemChanged(position)

                    }
                } else { // Quantity decreased
                    // Update stockItemDetails with the new quantity
                    Log.e("QTY FROM >0 else",stockItemDetails.toString() )
                    stockItemDetails[position] = updatedItem.copy()
                    // Calculate new balance quantity
                    val newBalanceQty = totalQty.toDouble() + (-qtyDifference)
                    val balQtyFormat = String.format("%.2f", newBalanceQty)
                    binding.tvBalQtyValue.setText(balQtyFormat)
                    stockItemDetails[position].originalQty=updatedItem.Qty
                    // Notify adapter about the item change
                    splitStockItemListAdapter!!.notifyItemChanged(position)
                }
            }

        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
*/

    private fun addExisitngNewItem(
        templist: MutableList<CustomSplitModel>,
        customSplitModel: CustomSplitModel
    ) {


    }

    private fun setProductItemDetails() {
        binding.clProdItemDetails.visibility = View.VISIBLE
        binding.tvItemCodeValue.setText(getStockItemDetailsOnBarcodeSplit.responseObject.barcode)

        binding.tvTotalQtyValue.setText(getStockItemDetailsOnBarcodeSplit.responseObject.stockQty.toString())
        var stockQtyString = getStockItemDetailsOnBarcodeSplit.responseObject.stockQty.toString()
        val stockQtyDouble = if ('.' !in stockQtyString) {
            "$stockQtyString.0".toDouble()
        } else {
            stockQtyString.toDouble()
        }

        balQty = stockQtyDouble
        totalQty = stockQtyDouble
        binding.tvBalQtyValue.setText(balQty.toString())
        binding.tvItemCodeDesc.setText("${getStockItemDetailsOnBarcodeSplit.responseObject.itemCode} - ${getStockItemDetailsOnBarcodeSplit.responseObject.description}")

    }

    private fun callSubmitApi() {
        try {
            val sumOfReceivedQtyIncludingUpdatedItem =
                stockItemDetails.sumByDouble {
                    it.Qty.toDouble()
                }
            if (sumOfReceivedQtyIncludingUpdatedItem < balQty.toDouble()) {
                Toast.makeText(
                    this, "Please Split all Quantity.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                submitSplit()
            }
        } catch (e: Exception) {
            Log.e("fromcallsubmitapi", e.message.toString())
            Toast.makeText(
                this@SplitingActivity,
                e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun generateBarcodeForExisitngItem() {
        try {
            viewModel.getBarcodeValueWithPrefix(token!!, baseUrl, "S")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }

    private fun getStockItemDetailOnBarcodeSplitNew() {
        try {
            viewModel.getBarcodeValueWithPrefixNew(token!!, baseUrl, "S")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }

    private fun submitSplit() {
        try {
            var splitStockLineItem: MutableList<SplitStockLineItem> = mutableListOf()
            for (i in stockItemDetails) {
                splitStockLineItem.add(SplitStockLineItem(i.Barcode, i.Qty))
            }

            if (splitStockLineItem.size > 0) {
                viewModel.splitStockItems(
                    token!!,
                    baseUrl,
                    SplitStockItemResquest(splitStockLineItem, stockItemId!!.toInt())
                )
            }

        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }

    private fun getProductItemDetails(data: String) {
        try {
            viewModel.getStockItemDetailOnBarcode(token!!, baseUrl, data)
        } catch (e: Exception) {
            runOnUiThread { Toasty.warning(this@SplitingActivity, e.message.toString()).show() }

        }
    }

    private fun clearAll() {
        stockItemDetails.clear()
        splitStockItemListAdapter!!.notifyDataSetChanged()
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

    /////barcode code
    private fun initBarcode() {
        isBarcodeInit = true
        Thread.sleep(1000)
        val results = EMDKManager.getEMDKManager(this@SplitingActivity, this)
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
                this@SplitingActivity,
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
                getProductItemDetails(it)
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