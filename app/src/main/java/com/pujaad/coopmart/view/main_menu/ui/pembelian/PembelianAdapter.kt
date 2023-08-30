package com.pujaad.coopmart.view.main_menu.ui.pembelian

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.databinding.ItemTransactionBinding
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.Pembelian

class PembelianAdapter: ListAdapter<Pembelian, PembelianAdapter.OrderViewHolder>(callback) {

    var listener: AdapterListener<Pembelian>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pembelian) = with(binding) {
            binding.tvInvoiceNumber.text = "${Constant.CODE_PEMBELIAN}-${ item.id }"
            binding.tvPrice.text = item.totalPrice.toIDRFormat()
            binding.tvDate.text = item.date
            
            binding.labelTransactionCustomer.visibility = View.GONE
            binding.tvCustomer.visibility = View.GONE

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Pembelian>() {
            override fun areItemsTheSame(oldItem: Pembelian, newItem: Pembelian): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Pembelian, newItem: Pembelian): Boolean {
                return oldItem == newItem
            }

        }
    }
}