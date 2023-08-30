package com.pujaad.coopmart.view.main_menu.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.api.common.ResourceState
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentInventoryBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.model.Product
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.InventoryViewModel


class InventoryFragment : BaseFragment(R.layout.fragment_inventory) {

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.Factory(
            requireActivity().application
        )
    }
    private lateinit var binding: FragmentInventoryBinding
    private var inventoryAdapter: InventoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lookupInventory("")
        inventoryAdapter = InventoryAdapter()
        inventoryAdapter?.listener = object : AdapterListener<Product> {
            override fun onclick(item: Product) {
                findNavController().navigate(InventoryFragmentDirections.inventoryToEditInventory(product = item))
            }

        }

        binding.rvInventory.layoutManager = LinearLayoutManager(context)
        binding.rvInventory.adapter = inventoryAdapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                ErrorType.INVENTORY_NOT_FOUND -> {
                    inventoryAdapter?.submitList(arrayListOf())
                    setEmptyState(true)
                }

                else -> Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.observableProducts.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                setEmptyState(it.data?.isEmpty() ?: false)
                inventoryAdapter?.submitList(it.data)
            }
        }

        binding.etSearchInventory.addTextChangedListener {
            viewModel.searchHandler.postDelayed({
                searchByUserQuery()
            }, viewModel.TYPING_DELAY)
        }

        binding.btnSearch.setOnClickListener {
            searchByUserQuery()
        }

//        if (getUserType() == 1) {
//            binding.fabAdd.visibility = View.GONE
//            return
//        }
        
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(InventoryFragmentDirections.inventoryToAddInventory())
        }

        viewModel.observableProducts.observe(viewLifecycleOwner) { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                val products = resource.data
                inventoryAdapter?.submitList(products)
                setEmptyState(products.isNullOrEmpty())

                val outOfStockProducts = products?.filter { it.stock <= 5 }
                if (outOfStockProducts != null && outOfStockProducts.isNotEmpty()) {
                    val dialogBuilder = AlertDialog.Builder(requireContext())
                    dialogBuilder.setTitle("Stok Produk Habis")
                    dialogBuilder.setMessage("Produk berikut habis stok:\n${getOutOfStockProductNames(outOfStockProducts)}")
                    dialogBuilder.setPositiveButton("OK", null)
                    dialogBuilder.show()
                }
            }
        }
//        viewModel.observableProducts.observe(viewLifecycleOwner) { resource ->
//            if (resource.status == ResourceState.SUCCESS) {
//                val products = resource.data
//                inventoryAdapter?.submitList(products)
//                setEmptyState(products.isNullOrEmpty())
//
//
//                val outOfStockProducts = products?.filter { it.stock <= 10 }
//                if (outOfStockProducts != null && outOfStockProducts.isNotEmpty()) {
//                    var message = "Produk berikut habis stok: "
//                    for (product in outOfStockProducts) {
//                        message += "${product.name} "
//                    }
//
//                    Toast.makeText(this@InventoryFragment.requireContext(), "$message" ,Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

    }

    private fun getOutOfStockProductNames(products: List<Product>): String {
        val productNames = products.joinToString(separator = "\n") { it.name }
        return productNames
    }
    private fun searchByUserQuery() {
        val query = binding.etSearchInventory.text.toString()
        viewModel.lookupInventory(query)
    }

    private fun setEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNotFound.visibility = View.VISIBLE

            return
        }

        binding.tvNotFound.visibility = View.GONE
    }

}