package com.company.iss.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleMapItemBinding
import com.company.iss.interfaces.MapItemClickListener
import com.company.iss.model.MapModel

class MapAdapter(private var itemList: ArrayList<MapModel>, private val itemClickListener: MapItemClickListener):
    RecyclerView.Adapter<MapAdapter.ItemViewHolder>() {

    fun submitList(itemList: ArrayList<MapModel>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_map_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleMapItemBinding.bind(itemView)

        fun onBind(currentItem: MapModel) {
            //set details
            binding.mapTitle.text = currentItem.map_title

            //main item click event
            binding.mapButton.setOnClickListener {
                itemClickListener.onMapButtonClick(currentItem)
            }
        }

    }
}