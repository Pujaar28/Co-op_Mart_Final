package com.pujaad.coopmart.view.main_menu.ui.PenjualanItem.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.databinding.ItemTransactionItemBinding
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.PenjualanItem

class PenjualanItemAdapter(isCanDelete: Boolean): ListAdapter<PenjualanItem, PenjualanItemAdapter.OrderViewHolder>(callback) {

    private val isCanDelete = isCanDelete

    var listener: AdapterListener<PenjualanItem>? = null
    var deleteListener: AdapterListener<PenjualanItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemTransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemTransactionItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PenjualanItem) = with(binding) {
            binding.tvItemName.text = item.product?.name ?: "Unknown Product"
            binding.tvQuantity.text = "(x${item.quantity})"
            val price = item.product?.sellPrice ?: 0
            binding.tvPrice.text = price.toIDRFormat()

            if (!isCanDelete)
                binding.ivDelete.visibility = View.GONE

            binding.ivDelete.setOnClickListener {
                deleteListener?.onclick(item)
            }

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<PenjualanItem>() {
            override fun areItemsTheSame(oldItem: PenjualanItem, newItem: PenjualanItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PenjualanItem, newItem: PenjualanItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}