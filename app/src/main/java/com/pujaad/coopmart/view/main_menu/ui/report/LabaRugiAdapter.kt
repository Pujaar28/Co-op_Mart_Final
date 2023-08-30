package com.pujaad.coopmart.view.main_menu.ui.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.databinding.ItemLabaRugiBinding
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.LabaRugi

class LabaRugiAdapter: ListAdapter<LabaRugi, LabaRugiAdapter.OrderViewHolder>(callback) {

    var listener: AdapterListener<LabaRugi>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemLabaRugiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemLabaRugiBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LabaRugi) = with(binding) {
            binding.tvDate.text = item.date
            binding.tvPembelian.text = item.pembelian.toIDRFormat()
            binding.tvPenjualan.text = item.penjualan.toIDRFormat()
            binding.tvPendapatan.text = item.pendapatan.toIDRFormat()

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<LabaRugi>() {
            override fun areItemsTheSame(oldItem: LabaRugi, newItem: LabaRugi): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: LabaRugi, newItem: LabaRugi): Boolean {
                return oldItem == newItem
            }

        }
    }
}