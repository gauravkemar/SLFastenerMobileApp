package com.example.slfastenermobileapp.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.model.split.CustomSplitModel

class SplitStockItemListAdapter(
    private val stockItemList: MutableList<CustomSplitModel>,
    private val onSave: (Int, CustomSplitModel) -> Unit,
    private val addItem: (CustomSplitModel) -> Unit,
    private val onDelete:(Int,CustomSplitModel)->Unit,
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
        if(stockItemMod.Qty.trim().equals("0.00")|| stockItemMod.Qty.trim().equals(""))
        {
            holder.edBarcodeValue.setHint("0.00")
        }
        else
        {
            holder.edBarcodeValue.setText(stockItemMod.Qty)
        }
        /*holder.clSave.setOnClickListener {
            stockItemMod.Qty =holder.edBarcodeValue.text.toString().trim()
            onSave(itemPosition,stockItemMod)
        }*/
        holder.clDublicate.setOnClickListener {
            addItem(stockItemMod)
        }
        holder.clPrintDelete.setOnClickListener {
            holder.edBarcodeValue.removeTextChangedListener(holder.textWatcher)
            onDelete(position ,stockItemMod)
            holder.edBarcodeValue.setText("")

      /*      stockItemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,stockItemList.size )*/
        }
        // Adding TextWatcher with valid position check
        holder.textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newPosition = holder.adapterPosition
                if (newPosition != RecyclerView.NO_POSITION) {
                    if (s.toString().trim().isNotEmpty()) {
                        stockItemList[newPosition].Qty = s.toString().trim()
                        holder.edBarcodeValue.setSelection(holder.edBarcodeValue.text.length)
                        onSave(newPosition, stockItemList[newPosition])
                    }
                }
            }
        }

        holder.edBarcodeValue.addTextChangedListener(holder.textWatcher)
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
        lateinit var textWatcher: TextWatcher

    }

}
