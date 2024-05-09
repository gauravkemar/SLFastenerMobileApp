package com.example.slfastenermobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.model.putaway.ResponseObjectX

class SplitStockItemListAdapter(private val stockItemList: MutableList<ResponseObjectX>) :
    RecyclerView.Adapter<SplitStockItemListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.split_list_item_layout, parent, false)
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
