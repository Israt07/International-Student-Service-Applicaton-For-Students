package com.company.iss.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.iss.R
import com.company.iss.databinding.SingleImageItemBinding
import com.company.iss.model.CommunityImageModel
import com.company.iss.utils.loadImage

class CommunityImageAdapter(private var itemList: ArrayList<CommunityImageModel>): RecyclerView.Adapter<CommunityImageAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_image_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = SingleImageItemBinding.bind(itemView)

        fun onBind(currentItem: CommunityImageModel) {
            //set details
            binding.image.loadImage(currentItem.image_link)
        }

    }
}