package com.pujaad.coopmart.view.main_menu.ui.inventory.transaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentInventoryTransactionBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.Product
import com.pujaad.coopmart.model.ProductCategory
import com.pujaad.coopmart.model.Supplier
import com.pujaad.coopmart.view.dialog.AlertCallback
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.inventory_category.lookup.ChooseInventoryCategoryDialog
import com.pujaad.coopmart.view.main_menu.ui.supplier.lookup.ChooseSupplierDialog
import com.pujaad.coopmart.viewmodel.InventoryViewModel


class InventoryTransactionFragment : BaseFragment(R.layout.fragment_inventory_transaction) {

    companion object {
        const val REQUEST_KEY = "INVENTORY_KEY"
    }

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.Factory(
            requireActivity().application
        )
    }
    private lateinit var binding: FragmentInventoryTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val product = arguments?.getParcelable<Product>("product")
        if (product != null) viewModel.setProduct(product)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = setFormtitle()

        setFragmentResultListener(ChooseInventoryCategoryDialog.REQUEST_KEY) { key, bundle ->
            val selectedCategory = bundle.getParcelable<ProductCategory>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.setSelectedCategory(selectedCategory)
        }

        setFragmentResultListener(ChooseSupplierDialog.REQUEST_KEY) { key, bundle ->
            val selectedSupplier = bundle.getParcelable<Supplier>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.setSelectedSupplier(selectedSupplier)
        }

        viewModel.observableProduct.observe(viewLifecycleOwner) {
            bindForm(it)
        }

        viewModel.observableSelectedProductCategory.observe(viewLifecycleOwner) {
            binding.tieProductCategory.setText(it)
        }

        viewModel.observableSelectedProductSupplier.observe(viewLifecycleOwner) {
            binding.tieSupplier.setText(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                else -> showAlertDialog(
                    "Operation Failed",
                    it.message.toStringOrEmpty(),
                    "OK",
                    null
                )
            }
        }

        viewModel.onSuccessSubmit.observe(viewLifecycleOwner) {
            if (!it) return@observe

            val operationType = if (viewModel.isAddFormState) "add" else "update"
            val message = "Success $operationType product!"
            showAlertDialog("Operation Success", message, "OK", object : AlertCallback {
                override fun onButtonClicked(dialog: Dialog) {
                    val bundle = Bundle()
                    bundle.putBoolean(Constant.OPERATION_STATUS, true)
                    setFragmentResult(REQUEST_KEY, bundle)
                    findNavController().navigateUp()
                }
            })

        }

        binding.tieSupplier.setOnClickListener {
            findNavController().navigate(InventoryTransactionFragmentDirections.transactionToChooseSupplier())
        }

        binding.tieProductCategory.setOnClickListener {
            findNavController().navigate(InventoryTransactionFragmentDirections.transactionToChooseCategory())
        }

        binding.btnSaveInventory.setOnClickListener {
            val name = binding.tieProductName.text.toString()
            val desc = binding.tieProductDesc.text.toString()
            val buyPriceText = binding.tieProductBuyPrice.text.toString()
            val sellPriceText = binding.tieProductSellPrice.text.toString()
            val stockText = binding.tieProductStock.text.toString()
            val buyPrice = buyPriceText.toIntOrNull()
            val sellPrice = sellPriceText.toIntOrNull()
            val stock = stockText.toIntOrNull()

            if (buyPrice != null && sellPrice != null && stock != null) {
                if (buyPrice != null && sellPrice != null && stock !=null) {
                    viewModel.submitProduct(
                        name = name,
                        desc = desc,
                        buyPrice = buyPrice,
                        sellPrice = sellPrice,
                        stock = stock
                    )
            } else {
                    Toast.makeText(this@InventoryTransactionFragment.requireActivity(), "Gagal Menambahkan", Toast.LENGTH_SHORT).show()
            }

            }
        }
    }

    private fun bindForm(item: Product) {
        binding.tieProductName.setText(item.name.toStringOrEmpty())
        binding.tieProductDesc.setText(item.description.toStringOrEmpty())
        binding.tieProductBuyPrice.setText(item.buyPrice.toString())
        binding.tieProductStock.setText(item.stock.toString())
        binding.tieProductSellPrice.setText(item.sellPrice.toString())
        binding.tieSupplier.setText(item.supplier?.name.toStringOrEmpty())
        binding.tieProductCategory.setText(item.category?.name.toStringOrEmpty())
        binding.tieProductStock.isEnabled = false

        if (getUserType() == 1) {
            binding.btnSaveInventory.visibility = View.INVISIBLE
            binding.tieProductName.isEnabled = false
            binding.tieProductDesc.isEnabled = false
            binding.tieProductBuyPrice.isEnabled = false
            binding.tieProductSellPrice.isEnabled = false
            binding.tieSupplier.isEnabled = false
            binding.tieProductCategory.isEnabled = false
        }
    }

    private fun setFormtitle(): String {
        return if (viewModel.isAddFormState)
            getString(R.string.tv_title_add_product)
        else
            if (getUserType() == 0)
                getString(R.string.tv_title_edit_product)
            else
                getString(R.string.tv_title_view_product)
    }
}