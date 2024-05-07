package com.example.slfastenermobileapp.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.generalresponserequest.GeneralResponse
import com.example.slfastenermobileapp.model.putaway.GetStockItemDetailOnBarcodeResponse
import com.example.slfastenermobileapp.model.putaway.ResponseObjectX
import com.example.slfastenermobileapp.repository.SLFastenerRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MergeStockItemListAdapter(private val stockItemList: MutableList<ResponseObjectX>) :
    RecyclerView.Adapter<MergeStockItemListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.merge_list_item_layout, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val stockItemMod: ResponseObjectX = stockItemList?.get(itemPosition)!!
        holder.tvItemCodeValue.setText(stockItemMod.itemCode)
        holder.tvItemDescrValue.setText(stockItemMod.description)
        holder.tvstockQtyValue.setText(stockItemMod.stockQty.toString())

    }



    override fun getItemCount(): Int {
        if (stockItemList.size == 0) {
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        } else {
            return stockItemList.size
        }
        return stockItemList.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvItemCodeValue: TextView = itemView.findViewById(R.id.tvItemCodeValue)
        val tvItemDescrValue: TextView = itemView.findViewById(R.id.tvItemNameValue)
        val tvstockQtyValue: TextView = itemView.findViewById(R.id.tvBarcodeValue)


    }

}
