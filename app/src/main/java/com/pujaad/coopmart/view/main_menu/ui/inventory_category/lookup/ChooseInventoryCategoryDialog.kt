package com.pujaad.coopmart.view.main_menu.ui.inventory_category.lookup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.api.common.ResourceState
import com.pujaad.coopmart.base.BaseDialogFragment
import com.pujaad.coopmart.databinding.FragmentChooseInventoryCategoryDialogBinding
import com.pujaad.coopmart.extension.dismissLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.model.ProductCategory
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.inventory_category.InventoryCategoryAdapter
import com.pujaad.coopmart.viewmodel.InventoryViewModel

class ChooseInventoryCategoryDialog : BaseDialogFragment() {

    companion object {
        const val REQUEST_KEY = "SELECTED_CATEGORY_KEY"
    }

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.Factory(
            requireActivity().application
        )
    }

    private lateinit var binding: FragmentChooseInventoryCategoryDialogBinding
    private var inventoryCategoryAdapter: InventoryCategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseInventoryCategoryDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCloseButton()

        viewModel.lookupInventoryCategory("")

        inventoryCategoryAdapter = InventoryCategoryAdapter(false)
        inventoryCategoryAdapter?.listener = object : AdapterListener<ProductCategory> {
            override fun onclick(item: ProductCategory) {
                val bundle = Bundle()
                bundle.putParcelable(Constant.SELECTED_ITEM, item)
                setFragmentResult(REQUEST_KEY, bundle)
                findNavController().navigateUp()
            }

        }

        binding.rvInventoryCategory.layoutManager = LinearLayoutManager(context)
        binding.rvInventoryCategory.adapter = inventoryCategoryAdapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else dismissLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                ErrorType.INVENTORY_NOT_FOUND -> {
                    setEmptyState(true)
                }

                else -> Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.observableProductsCategory.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                setEmptyState(it.data?.isEmpty() ?: false)
                inventoryCategoryAdapter?.submitList(it.data)
            }
        }

        binding.etSearchInventoryCategory.addTextChangedListener {
            viewModel.searchHandler.postDelayed({
                searchByUserQuery()
            }, viewModel.TYPING_DELAY)
        }

        binding.btnSearch.setOnClickListener {
            searchByUserQuery()
        }
    }

    private fun searchByUserQuery() {
        val query = binding.etSearchInventoryCategory.text.toString()
        viewModel.lookupInventoryCategory(query)
    }

    private fun setEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNotFound.visibility = View.VISIBLE
            return
        }

        binding.tvNotFound.visibility = View.GONE
    }

}