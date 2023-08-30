package com.pujaad.coopmart.view.main_menu.ui.pembelian.invoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pujaad.coopmart.databinding.ItemInvoiceBinding
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.PembelianItem

class PembelianInvoiceAdapter(): ListAdapter<PembelianItem, PembelianInvoiceAdapter.OrderViewHolder>(
    callback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemInvoiceBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PembelianItem) = with(binding) {
            binding.tvItemName.text = item.product?.name ?: "Unknown Product"
            binding.tvItemQuantity.text = item.quantity.toString()
            val price = item.product?.buyPrice?: 0
            binding.tvItemPrice.text = price.toIDRFormat()
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<PembelianItem>() {
            override fun areItemsTheSame(oldItem: PembelianItem, newItem: PembelianItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PembelianItem, newItem: PembelianItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}