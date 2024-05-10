package com.example.slfastenermobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.helper.Constants
import com.example.slfastenermobileapp.model.putaway.ResponseObjectX
import com.example.slfastenermobileapp.model.split.CustomSplitModel
import com.example.slfastenermobileapp.model.split.SplitStockLineItem

class SplitStockItemListAdapter(
    private val stockItemList: MutableList<CustomSplitModel>,
    private val onSave: (Int, CustomSplitModel) -> Unit,
    private val addItem: (CustomSplitModel) -> Unit,
    private val onDelete:(Int)->Unit,
) :
    RecyclerView.Adapter<SplitStockItemListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.split_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val stockItemMod: CustomSplitModel = stockItemList?.get(itemPosition)!!
        holder.tvItemCodeValue.setText(stockItemMod.Barcode)
        holder.edBarcodeValue.setText(stockItemMod.Qty)
        holder.clSave.setOnClickListener {
            stockItemMod.Qty =holder.edBarcodeValue.text.toString().trim()
            onSave(itemPosition,stockItemMod)
        }
        holder.clDublicate.setOnClickListener {
            addItem(stockItemMod)
        }
        holder.clPrintDelete.setOnClickListener {
            onDelete(itemPosition)
            stockItemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, stockItemList.size)
        }

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
        val edBarcodeValue: EditText = itemView.findViewById(R.id.tvBarcodeValue)
        val clSave: ConstraintLayout = itemView.findViewById(R.id.clSave)
        val clDublicate: ConstraintLayout = itemView.findViewById(R.id.clDublicate)
        val clPrintDelete: ConstraintLayout = itemView.findViewById(R.id.clPrintDelete)


    }

}
