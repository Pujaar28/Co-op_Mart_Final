package com.pujaad.coopmart.view.main_menu.ui.penjualan.add_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentAddPenjualanItemBinding
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.Product
import com.pujaad.coopmart.view.main_menu.ui.inventory.lookup.ChooseInventoryDialog
import com.pujaad.coopmart.view.main_menu.ui.inventory.scanner.InventoryScannerFragment
import com.pujaad.coopmart.viewmodel.AddPenjualanItemViewModel

class AddPenjualanItemFragment : BaseFragment(R.layout.fragment_add_penjualan_item) {

    companion object {
        const val REQUEST_KEY = "SELECTED_PENJUALAN_ITEM_KEY"
    }

    private val viewModel: AddPenjualanItemViewModel by viewModels { AddPenjualanItemViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentAddPenjualanItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddPenjualanItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = "Add New Item"

        setFragmentResultListener(ChooseInventoryDialog.REQUEST_KEY) { key, bundle ->
            val selectedProduct = bundle.getParcelable<Product>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.setPenjualanItem(selectedProduct)
        }

        setFragmentResultListener(InventoryScannerFragment.REQUEST_KEY) { key, bundle ->
            val selectedProduct = bundle.getParcelable<Product>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.setPenjualanItem(selectedProduct)
        }

        viewModel.observableFormActiveState.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivAddQuantity.visibility = View.VISIBLE
                binding.ivRemoveQuantity.visibility = View.VISIBLE
                binding.btnSubmitForm.isEnabled = it
            }
        }

        viewModel.observableMaxQuantity.observe(viewLifecycleOwner) {
            binding.tvMaxQuantity.text = it.toString()
        }

        viewModel.observableQuantity.observe(viewLifecycleOwner) {
            binding.tvQuantity.text = it.toString()
        }

        viewModel.observableItemPrice.observe(viewLifecycleOwner) {
            binding.tvPrice.text = it.toIDRFormat()
        }

        viewModel.observableProductName.observe(viewLifecycleOwner) {
            binding.tieProduct.setText(it)
        }

        binding.tieProduct.setOnClickListener {
            findNavController().navigate(AddPenjualanItemFragmentDirections.addPenjualanToChooseProduct())
        }

        binding.ivScanProduct.setOnClickListener {
            findNavController().navigate(AddPenjualanItemFragmentDirections.addPenjualanToScanInventory())
        }

        binding.ivAddQuantity.setOnClickListener {
            viewModel.increasePenjualanItemQuantity()
        }

        binding.ivRemoveQuantity.setOnClickListener {
            viewModel.decreasePenjualanItemQuantity()
        }

        binding.btnSubmitForm.setOnClickListener {
            val bundle = Bundle()
            val item = viewModel.selectedPenjualanItem
            if (item.quantity <= 0) {
                showAlertDialog("Warning", "Item quantity cannot be empty", "OK", null)
                return@setOnClickListener
            }
            bundle.putParcelable(Constant.SELECTED_ITEM, item)
            setFragmentResult(REQUEST_KEY, bundle)
            findNavController().navigateUp()
        }
    }
}