package com.example.slfastenermobileapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastenermobileapp.R
import com.example.slfastenermobileapp.model.merge.StockItemResponse

class ReceivingItemAdapter (
    private val context: Context,
    private val stockItemList: MutableList<StockItemResponse>,
    private val onDelete: (Int, StockItemResponse) -> Unit,
) :
    RecyclerView.Adapter<ReceivingItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.merge_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val stockItemMod: StockItemResponse = stockItemList?.get(itemPosition)!!
        holder.tvItemBarcodeValue.setText(stockItemMod.barcode)
        holder.tvItemQtyValue.setText(stockItemMod.stockQty.toString())
        holder.delIcon.setOnClickListener {
            onDelete(position,stockItemMod)
            /*          stockItemList.removeAt(position)
                      notifyItemRangeChanged(position,stockItemList.size)*/
        }
        holder.tvItemDescValue.setText("${stockItemMod.itemCode} - ${stockItemMod.description}")
        if(stockItemMod.isSaved)
        {
            holder.delIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.print_icon))
        }
        else
        {
            holder.delIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.delete_icon))
        }
        if(stockItemMod.isMerged)
        {
            holder.delIcon.visibility= View.GONE
        }
        else
        {
            holder.delIcon.visibility= View.VISIBLE
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
        val tvItemBarcodeValue: TextView = itemView.findViewById(R.id.tvItemBarcodeValue)
        val tvItemQtyValue: TextView = itemView.findViewById(R.id.tvItemQtyValue)
        val delIcon: ImageView = itemView.findViewById(R.id.delIcon)
        val tvItemDescValue: TextView = itemView.findViewById(R.id.tvItemDescValue)


    }

}
