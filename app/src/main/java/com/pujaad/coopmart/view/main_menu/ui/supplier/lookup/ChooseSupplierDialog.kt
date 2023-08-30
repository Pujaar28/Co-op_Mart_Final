package com.pujaad.coopmart.view.main_menu.ui.supplier.lookup

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
import com.pujaad.coopmart.databinding.FragmentChooseSupplierDialogBinding
import com.pujaad.coopmart.extension.dismissLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.model.Supplier
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.supplier.SupplierAdapter
import com.pujaad.coopmart.viewmodel.SupplierViewModel

class ChooseSupplierDialog : BaseDialogFragment() {

    companion object {
        const val REQUEST_KEY = "SELECTED_SUPPLIER_KEY"
    }

    private val viewModel: SupplierViewModel by viewModels { SupplierViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentChooseSupplierDialogBinding
    private var supplierAdapter: SupplierAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseSupplierDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCloseButton()

        viewModel.lookupSupplier("")

        supplierAdapter = SupplierAdapter(false)
        supplierAdapter?.listener = object : AdapterListener<Supplier> {
            override fun onclick(item: Supplier) {
                val bundle = Bundle()
                bundle.putParcelable(Constant.SELECTED_ITEM, item)
                setFragmentResult(REQUEST_KEY, bundle)
                findNavController().navigateUp()
            }

        }

        binding.rvSupplier.layoutManager = LinearLayoutManager(context)
        binding.rvSupplier.adapter = supplierAdapter

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

        viewModel.observableSuppliers.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                setEmptyState(it.data?.isEmpty() ?: false)
                supplierAdapter?.submitList(it.data)
            }
        }

        binding.etSearchSupplier.addTextChangedListener {
            viewModel.searchHandler.postDelayed({
                searchByUserQuery()
            }, viewModel.TYPING_DELAY)
        }

        binding.btnSearch.setOnClickListener {
            searchByUserQuery()
        }
    }

    private fun searchByUserQuery() {
        val query = binding.etSearchSupplier.text.toString()
        viewModel.lookupSupplier(query)
    }

    private fun setEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNotFound.visibility = View.VISIBLE
            return
        }

        binding.tvNotFound.visibility = View.GONE
    }
}