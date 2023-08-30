package com.pujaad.coopmart.view.main_menu.ui.pembelian.add_item

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentAddPembelianItemBinding
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.model.Product
import com.pujaad.coopmart.view.main_menu.ui.inventory.lookup.ChooseInventoryDialog
import com.pujaad.coopmart.view.main_menu.ui.inventory.scanner.InventoryScannerFragment
import com.pujaad.coopmart.viewmodel.AddPembelianItemViewModel

class AddPembelianItemFragment : BaseFragment(R.layout.fragment_add_pembelian_item) {

    companion object {
        const val REQUEST_KEY = "SELECTED_PEMBELIAN_ITEM_KEY"
    }

    private val viewModel: AddPembelianItemViewModel by viewModels { AddPembelianItemViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentAddPembelianItemBinding

    private val cameraPermissionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddPembelianItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = "Add New Item"

        setFragmentResultListener(ChooseInventoryDialog.REQUEST_KEY) { key, bundle ->
            val selectedProduct = bundle.getParcelable<Product>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.setPembelianItem(selectedProduct)
        }

        setFragmentResultListener(InventoryScannerFragment.REQUEST_KEY) { key, bundle ->
            val selectedProduct = bundle.getParcelable<Product>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.setPembelianItem(selectedProduct)
        }

        viewModel.observableFormActiveState.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivAddQuantity.visibility = View.VISIBLE
                binding.ivRemoveQuantity.visibility = View.VISIBLE
                binding.btnSubmitForm.isEnabled = it
            }
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
            findNavController().navigate(AddPembelianItemFragmentDirections.addPembelianToChooseProduct())
        }

        binding.ivScanProduct.setOnClickListener {
            startScanning()
        }

        binding.ivAddQuantity.setOnClickListener {
            viewModel.increasePembelianItemQuantity()
        }

        binding.ivRemoveQuantity.setOnClickListener {
            viewModel.decreasePembelianItemQuantity()
        }

        binding.btnSubmitForm.setOnClickListener {
            val bundle = Bundle()
            val item = viewModel.selectedPembelianItem
            if (item.quantity <= 0) {
                showAlertDialog("Warning", "Item quantity cannot be empty", "OK", null)
                return@setOnClickListener
            }
            bundle.putParcelable(Constant.SELECTED_ITEM, item)
            setFragmentResult(REQUEST_KEY, bundle)
            findNavController().navigateUp()
        }
    }

    private fun startScanning() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            findNavController().navigate(AddPembelianItemFragmentDirections.addPembelianToScanInventory())
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        }
    }
}