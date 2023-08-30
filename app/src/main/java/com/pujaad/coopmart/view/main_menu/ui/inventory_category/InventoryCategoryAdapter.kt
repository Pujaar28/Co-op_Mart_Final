package com.pujaad.coopmart.view.main_menu.ui.inventory_category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.databinding.ItemInventoryCategoryBinding
import com.pujaad.coopmart.model.ProductCategory

class InventoryCategoryAdapter(isCanDelete: Boolean = true): ListAdapter<ProductCategory, InventoryCategoryAdapter.OrderViewHolder>(callback) {

    val isCanDelete = isCanDelete
    var listener: AdapterListener<ProductCategory>? = null
    var deleteListener: AdapterListener<ProductCategory>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemInventoryCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemInventoryCategoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductCategory) = with(binding) {
            binding.tvCategoryName.text = item.name

//            if (!isCanDelete) {
//                binding.ivClose.visibility = View.GONE
//            }
//            binding.ivClose.setOnClickListener {
//                deleteListener?.onclick(item)
//            }

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<ProductCategory>() {
            override fun areItemsTheSame(oldItem: ProductCategory, newItem: ProductCategory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ProductCategory, newItem: ProductCategory): Boolean {
                return oldItem == newItem
            }

        }
    }
}