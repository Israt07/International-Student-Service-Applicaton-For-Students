package com.company.iss.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleHomeMenuItemBinding
import com.company.iss.interfaces.HomeMenuItemClickListener
import com.company.iss.model.HomeMenuModel
import com.company.iss.utils.loadImage

class HomeMenuAdapter(private var itemList: ArrayList<HomeMenuModel>, private val itemClickListener: HomeMenuItemClickListener):
    RecyclerView.Adapter<HomeMenuAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_home_menu_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleHomeMenuItemBinding.bind(itemView)

        fun onBind(currentItem: HomeMenuModel) {
            //set details
            binding.itemIconImageview.loadImage(currentItem.menu_icon)
            binding.itemTitleTextview.text = currentItem.menu_title

            //main item click event
            itemView.setOnClickListener {
                itemClickListener.onItemClick(currentItem)
            }
        }

    }
}