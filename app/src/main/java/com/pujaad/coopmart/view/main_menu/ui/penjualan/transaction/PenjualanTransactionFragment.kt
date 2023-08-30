package com.pujaad.coopmart.view.main_menu.ui.penjualan.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentPenjualanTransactionBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toAppDateFormat
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.Penjualan
import com.pujaad.coopmart.model.PenjualanItem
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.PenjualanItem.transaction.PenjualanItemAdapter
import com.pujaad.coopmart.view.main_menu.ui.inventory.transaction.InventoryTransactionFragment
import com.pujaad.coopmart.view.main_menu.ui.pembelian.transaction.PembelianTransactionFragmentDirections
import com.pujaad.coopmart.view.main_menu.ui.penjualan.add_item.AddPenjualanItemFragment
import com.pujaad.coopmart.viewmodel.PenjualanViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class PenjualanTransactionFragment : BaseFragment(R.layout.fragment_penjualan_transaction) {

    private val viewModel: PenjualanViewModel by viewModels {
        PenjualanViewModel.Factory(
            requireActivity().application
        )
    }
    private lateinit var binding: FragmentPenjualanTransactionBinding
    private var adapter: PenjualanItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val penjualan = arguments?.getParcelable<Penjualan>("penjualan")
        viewModel.setPenjualan(penjualan)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPenjualanTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = setFormtitle()

        setFormDate()
        setFormState()

        setFragmentResultListener(AddPenjualanItemFragment.REQUEST_KEY) { key, bundle ->
            val selectedProduct = bundle.getParcelable<PenjualanItem>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.addPenjualanItem(selectedProduct)
        }

        adapter = PenjualanItemAdapter(viewModel.isAddFormState)
        adapter?.listener = object : AdapterListener<PenjualanItem> {
            override fun onclick(item: PenjualanItem) {
                // TODO("Not yet implemented")
            }
        }

        adapter?.deleteListener = object : AdapterListener<PenjualanItem> {
            override fun onclick(item: PenjualanItem) {
                viewModel.deletePenjualanItem(item)
            }
        }

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = this.adapter

        viewModel.observablePenjualan.observe(viewLifecycleOwner) {
            bindForm(it)
        }

        viewModel.observablePenjualanItems.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                adapter?.submitList(null)
                adapter?.notifyDataSetChanged()
                return@observe
            }
            adapter?.submitList(it)
            adapter?.notifyDataSetChanged()
        }

        viewModel.observableTotalPrice.observe(viewLifecycleOwner) {
            binding.tvTotalPrice.text = it
        }
        viewModel.observableTotalChanges.observe(viewLifecycleOwner) {
            binding.tvPaymentChanges.text = it
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
            if (!it.first) return@observe

            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Do you want to review transaction invoice?")
            builder.setCancelable(true)
            builder.setNegativeButton("No") { dialog, which ->
                val bundle = Bundle()
                bundle.putBoolean(Constant.OPERATION_STATUS, true)
                setFragmentResult(InventoryTransactionFragment.REQUEST_KEY, bundle)
                findNavController().navigateUp()
            }
            builder.setPositiveButton("Yes") { dialog, which ->
                findNavController().navigate(
                    PenjualanTransactionFragmentDirections.transactionToInvoiceFromAdd(
                        it.second,
                        Constant.CODE_PEMBELIAN
                    )
                )
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        if (viewModel.isAddFormState)
            binding.tvCashierName.text = viewModel.getName()

        binding.btnSubmitForm.setOnClickListener {
            if (!viewModel.isAddFormState) {
                if (viewModel.selectedPenjualan == null) {
                    showAlertDialog("Warning", "No invoice data found", "OK", null)
                    return@setOnClickListener
                }
                val id = viewModel.selectedPenjualan.id
                findNavController().navigate(
                    PenjualanTransactionFragmentDirections.transactionToInvoice(
                        id
                    )
                )
                return@setOnClickListener
            }

            val customerName = binding.etCustomerName.text.toString()
            val customerPayment = binding.etCustomerPayment.text.toString().toIntOrNull() ?: 0
            viewModel.submitPenjualan(customerName = customerName, paymentReceived = customerPayment)
        }

        if (viewModel.isAddFormState) {
            binding.etCustomerPayment.addTextChangedListener {
                var userPayment = it.toString().toIntOrNull() ?: 0
                if (userPayment < 0) userPayment = 0
                viewModel.notifyUserPaymentChange(userPayment)

            }
        }

        binding.ivAddItem.setOnClickListener {
            findNavController().navigate(PembelianTransactionFragmentDirections.transactionToAddItem())
        }
    }

    private fun bindForm(item: Penjualan) {
        binding.tvInvoice.text = "${Constant.CODE_PENJUALAN}-${item.id}"
        binding.tvDate.text = item.date.toAppDateFormat()
        binding.tvTotalPrice.text = item.totalPrice.toIDRFormat()
        binding.etCustomerPayment.setText(item.paymentReceived.toString())
        binding.tvPaymentChanges.text = item.paymentChanges.toIDRFormat()
        if (!viewModel.isAddFormState) {
            binding.etCustomerName.setText(item.customerName)
            binding.etCustomerName.isEnabled = false
            binding.etCustomerPayment.isEnabled = false
            binding.tvCashierName.text = item.karyawan?.name
        }

        adapter?.submitList(item.items)
    }

    private fun setFormDate() {
        if (viewModel.isAddFormState) {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat(Constant.APP_DATE_FORMAT)
            val current = formatter.format(time)
            binding.tvDate.text = current
        }
    }

    private fun setFormState() {
        if (!viewModel.isAddFormState) {
            binding.ivAddItem.visibility = View.GONE
            binding.btnSubmitForm.text = getString(R.string.txt_view_receipt)
            return
        }

        binding.ivAddItem.visibility = View.VISIBLE
        binding.tvInvoice.text = getString(R.string.txt_default_text)
        binding.tvTotalPrice.text = getString(R.string.txt_default_price)
    }

    private fun setFormtitle(): String {
        return if (viewModel.isAddFormState)
            getString(R.string.tv_title_add_penjualan)
        else
            getString(R.string.tv_title_view_penjualan)
    }

}