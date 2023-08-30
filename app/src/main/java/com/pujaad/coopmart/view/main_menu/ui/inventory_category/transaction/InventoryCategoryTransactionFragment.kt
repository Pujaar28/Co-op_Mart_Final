package com.pujaad.coopmart.view.main_menu.ui.inventory_category.transaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseDialogFragment
import com.pujaad.coopmart.databinding.FragmentInventoryCategoryTransactionBinding
import com.pujaad.coopmart.extension.dismissLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.ProductCategory
import com.pujaad.coopmart.view.dialog.AlertCallback
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.InventoryViewModel

class InventoryCategoryTransactionFragment : BaseDialogFragment() {

    companion object {
        const val REQUEST_KEY = "CATEGORY_KEY"
    }

    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModel.Factory(
            requireActivity().application
        )
    }
    private lateinit var binding: FragmentInventoryCategoryTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val category = arguments?.getParcelable<ProductCategory>("category")
        if (category != null) viewModel.setCategory(category)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryCategoryTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFormtitle()
        setCloseButton()

        viewModel.observableProductCategory.observe(viewLifecycleOwner) {
            bindForm(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else dismissLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                else -> showAlertDialog("Operation Failed", it.message.toStringOrEmpty(), "OK", null)
            }
        }

        viewModel.onSuccessSubmit.observe(viewLifecycleOwner) {
            if (!it) return@observe

            val operationType = if (viewModel.isAddFormState) "add" else "update"
            val message = "Success $operationType inventory category!"
            showAlertDialog("Operation Success", message, "OK", object : AlertCallback {
                override fun onButtonClicked(dialog: Dialog) {
                    val bundle = Bundle()
                    bundle.putBoolean(Constant.OPERATION_STATUS, true)
                    setFragmentResult(REQUEST_KEY, bundle)
                    findNavController().navigateUp()
                }
            })

        }

        binding.btnSubmitForm.setOnClickListener {
            val name = binding.tieName.text.toString()
            val desc = binding.tieDescription.text.toString()
            viewModel.submitCategory(name, desc)
        }

    }

    private fun setFormtitle() {
        binding.tvFormTitle.text = if (viewModel.isAddFormState)
            getString(R.string.tv_title_add_category)
        else
            getString(R.string.tv_title_edit_category)
    }

    private fun bindForm(category: ProductCategory) {
        binding.tieName.setText(category.name)
        binding.tieDescription.setText(category.description)
    }
}