package com.pujaad.coopmart.view.main_menu.ui.report

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentReportBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.dismissLoading
import com.pujaad.coopmart.extension.getDateInStr
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.view.dialog.DateDialog
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.inventory.InventoryAdapter
import com.pujaad.coopmart.view.main_menu.ui.pembelian.PembelianAdapter
import com.pujaad.coopmart.view.main_menu.ui.penjualan.PenjualanAdapter
import com.pujaad.coopmart.viewmodel.ReportViewModel
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Calendar

class ReportFragment : BaseFragment(R.layout.fragment_report) {

    private val viewModel: ReportViewModel by viewModels { ReportViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentReportBinding

    private var pembelianAdapter: PembelianAdapter? = null
    private var penjualanAdapter: PenjualanAdapter? = null
    private var productAdapter: InventoryAdapter? = null
    private var labaRugiAdapter: LabaRugiAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        penjualanAdapter = PenjualanAdapter()
        pembelianAdapter = PembelianAdapter()
        productAdapter = InventoryAdapter()
        labaRugiAdapter = LabaRugiAdapter()

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }
                else -> {
                    Toast.makeText(requireContext(), it.message.toStringOrEmpty(), Toast.LENGTH_SHORT).show()
                    binding.tvNotFound.visibility = View.VISIBLE
                    binding.fabSave.visibility = View.GONE
                }
            }

            dismissLoading()
        }

        viewModel.observableReport.observe(viewLifecycleOwner) {
            if (it.pembelianList.isEmpty() && it.penjualanList.isEmpty() && it.productList.isEmpty() && it.labaRugiList.isEmpty()) {
                binding.tvNotFound.visibility = View.VISIBLE
                binding.fabSave.visibility = View.GONE

                pembelianAdapter?.submitList(it.pembelianList)
                pembelianAdapter?.notifyDataSetChanged()

                penjualanAdapter?.submitList(it.penjualanList)
                penjualanAdapter?.notifyDataSetChanged()

                productAdapter?.submitList(it.productList)
                productAdapter?.notifyDataSetChanged()

                labaRugiAdapter?.submitList(it.labaRugiList)
                labaRugiAdapter?.notifyDataSetChanged()
                return@observe
            }

            if (it.pembelianList.isNotEmpty()) {
                binding.rvReport.layoutManager = LinearLayoutManager(context)
                binding.rvReport.adapter = pembelianAdapter

                pembelianAdapter?.submitList(it.pembelianList)
                pembelianAdapter?.notifyDataSetChanged()
            } else if (it.penjualanList.isNotEmpty()) {
                binding.rvReport.layoutManager = LinearLayoutManager(context)
                binding.rvReport.adapter = penjualanAdapter

                penjualanAdapter?.submitList(it.penjualanList)
                penjualanAdapter?.notifyDataSetChanged()
            } else if (it.productList.isNotEmpty()) {
                binding.rvReport.layoutManager = LinearLayoutManager(context)
                binding.rvReport.adapter = productAdapter

                productAdapter?.submitList(it.productList)
                productAdapter?.notifyDataSetChanged()
            } else {
                binding.rvReport.layoutManager = LinearLayoutManager(context)
                binding.rvReport.adapter = labaRugiAdapter

                labaRugiAdapter?.submitList(it.labaRugiList)
                labaRugiAdapter?.notifyDataSetChanged()
            }

            binding.tvNotFound.visibility = View.GONE
            binding.fabSave.visibility = View.VISIBLE
        }

        viewModel.observableReportPDF.observe(viewLifecycleOwner) {
            saveFile(it)
        }

        binding.tieFromDate.setOnClickListener {
            DateDialog.createDialogWithoutDateField(requireContext()
            ) { datePicker, p1, p2, p3 -> binding.tieFromDate.setText(datePicker!!.getDateInStr()) }.show()
        }

        binding.tieUntilDate.setOnClickListener {
            DateDialog.createDialogWithoutDateField(requireContext()
            ) { datePicker, p1, p2, p3 -> binding.tieUntilDate.setText(datePicker!!.getDateInStr()) }.show()
        }

        binding.btnSubmitForm.setOnClickListener {
            val startDate = binding.tieFromDate.text.toString()
            val endDate = binding.tieUntilDate.text.toString()
            val type = binding.spReportType.selectedItemId.toInt()
            viewModel.submitReport(startDate = startDate, endDate = endDate, type = type)
        }

        binding.btnClearForm.setOnClickListener {
            binding.tieFromDate.setText("")
            binding.tieUntilDate.setText("")
            binding.spReportType.setSelection(0, true)
            viewModel.resetReport()
        }

        binding.fabSave.setOnClickListener {
            viewModel.getResultPDF()
        }
    }

    fun saveFile(input: InputStream) {
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!path.exists()) {
                path.mkdirs()
            }
            val fos = FileOutputStream(path.absolutePath + "/report_" + Calendar.getInstance().timeInMillis + ".pdf")
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            showAlertDialog(
                "Success",
                "File saved in ${path.absolutePath}",
                "OK",
                null
            )
        }catch (e:Exception){
            Log.e("saveFile",e.toString())
            showAlertDialog(
                "Save Failed",
                "Failed to save report PDF, make sure you set permission to save file",
                "OK",
                null
            )
        }
        finally {
            input.close()
        }
    }
}