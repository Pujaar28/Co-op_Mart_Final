package com.pujaad.coopmart.view.main_menu.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.databinding.ItemUserBinding
import com.pujaad.coopmart.model.Karyawan

class UserAdapter: ListAdapter<Karyawan, UserAdapter.OrderViewHolder>(callback) {

    var listener: AdapterListener<Karyawan>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class OrderViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Karyawan) = with(binding) {
            binding.tvUserName.text = item.name
            binding.tvUserUsername.text = item.username

            binding.root.setOnClickListener {
                listener?.onclick(item)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Karyawan>() {
            override fun areItemsTheSame(oldItem: Karyawan, newItem: Karyawan): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Karyawan, newItem: Karyawan): Boolean {
                return oldItem == newItem
            }

        }
    }
}