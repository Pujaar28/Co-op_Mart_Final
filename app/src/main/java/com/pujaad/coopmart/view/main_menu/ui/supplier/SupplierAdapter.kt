package com.pujaad.coopmart.view.main_menu.ui.supplier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.databinding.ItemSupplierBinding
import com.pujaad.coopmart.model.Supplier

class SupplierAdapter(isCanDelete: Boolean = true): ListAdapter<Supplier, SupplierAdapter.OrderViewHolder>(callback) {
    private val isCanDelete = isCanDelete
    var listener: AdapterListener<Supplier>? = null
    var deleteListener: AdapterListener<Supplier>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemSupplierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemSupplierBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Supplier) = with(binding) {
            binding.tvSupplierName.text = item.name
            binding.tvSupplierCode.text = item.code
            if (!isCanDelete) {
                binding.ivClose.visibility = View.GONE
            }
            binding.ivClose.setOnClickListener {
                deleteListener?.onclick(item)
            }

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Supplier>() {
            override fun areItemsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
                return oldItem == newItem
            }

        }
    }
}