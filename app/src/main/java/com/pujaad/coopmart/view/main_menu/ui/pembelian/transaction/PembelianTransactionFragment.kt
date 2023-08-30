package com.pujaad.coopmart.view.main_menu.ui.pembelian.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.pujaad.coopmart.databinding.FragmentPembelianTransactionBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toAppDateFormat
import com.pujaad.coopmart.extension.toIDRFormat
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.Pembelian
import com.pujaad.coopmart.model.PembelianItem
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.PembelianItem.transaction.PembelianItemAdapter
import com.pujaad.coopmart.view.main_menu.ui.inventory.transaction.InventoryTransactionFragment
import com.pujaad.coopmart.view.main_menu.ui.pembelian.add_item.AddPembelianItemFragment
import com.pujaad.coopmart.viewmodel.PembelianViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class PembelianTransactionFragment : BaseFragment(R.layout.fragment_pembelian_transaction) {

    companion object {
        const val REQUEST_KEY = "PEMBELIAN_KEY"
    }

    private val viewModel: PembelianViewModel by viewModels { PembelianViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentPembelianTransactionBinding
    private var adapter: PembelianItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pembelian = arguments?.getParcelable<Pembelian>("pembelian")
        viewModel.setPembelian(pembelian)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPembelianTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = setFormtitle()

        setFormDate()
        setFormState()

        setFragmentResultListener(AddPembelianItemFragment.REQUEST_KEY) { key, bundle ->
            val selectedProduct = bundle.getParcelable<PembelianItem>(Constant.SELECTED_ITEM)
                ?: return@setFragmentResultListener
            viewModel.addPembelianItem(selectedProduct)
        }

        adapter = PembelianItemAdapter(viewModel.isAddFormState)
        adapter?.listener = object : AdapterListener<PembelianItem> {
            override fun onclick(item: PembelianItem) {
                // TODO("Not yet implemented")
            }
        }

        adapter?.deleteListener = object : AdapterListener<PembelianItem> {
            override fun onclick(item: PembelianItem) {
                viewModel.deletePembelianItem(item)
            }
        }

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = this.adapter

        viewModel.observablePembelian.observe(viewLifecycleOwner) {
            bindForm(it)
        }

        viewModel.observablePembelianItems.observe(viewLifecycleOwner) {
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
                findNavController().navigate(PembelianTransactionFragmentDirections.transactionToInvoiceFromAdd(it.second, Constant.CODE_PEMBELIAN))
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        if (viewModel.isAddFormState)
            binding.tvAdminName.text = viewModel.getName()

        binding.btnSubmitForm.setOnClickListener {
            if (!viewModel.isAddFormState) {
                if (viewModel.selectedPembelian == null) {
                    showAlertDialog("Warning", "No invoice data found", "OK", null)
                    return@setOnClickListener
                }
                val id = viewModel.selectedPembelian.id
                findNavController().navigate(PembelianTransactionFragmentDirections.transactionToInvoice(id))
                return@setOnClickListener
            }

            viewModel.submitPembelian()
        }

        binding.ivAddItem.setOnClickListener {
            findNavController().navigate(PembelianTransactionFragmentDirections.transactionToAddItem())
        }
    }

    private fun bindForm(item: Pembelian) {
        binding.tvInvoice.text = "PM-${ item.id }"
        binding.tvDate.text = item.date.toAppDateFormat()
        binding.tvTotalPrice.text = item.totalPrice.toIDRFormat()
        if (!viewModel.isAddFormState)
            binding.tvAdminName.text = item.karyawan?.name

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
            getString(R.string.tv_title_add_pembelian)
        else
            getString(R.string.tv_title_view_pembelian)
    }

}