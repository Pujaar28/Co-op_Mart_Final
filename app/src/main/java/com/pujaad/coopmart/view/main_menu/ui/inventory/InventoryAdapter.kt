package com.pujaad.coopmart.view.main_menu.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.databinding.ItemInventoryBinding
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.Product

class InventoryAdapter: ListAdapter<Product, InventoryAdapter.OrderViewHolder>(callback) {

    var listener: AdapterListener<Product>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemInventoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) = with(binding) {
            binding.tvIventoryName.text = item.name
            binding.tvPrice.text = item.sellPrice.toIDRFormat()
            binding.tvStock.text = item.stock.toString()
            binding.tvCode.text = "PRD-${ item.id }"

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

        }
    }
}