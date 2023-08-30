package com.pujaad.coopmart.view.main_menu.ui.penjualan.invoice

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentPenjualanInvoiceBinding
import com.pujaad.coopmart.extension.*
import com.pujaad.coopmart.model.Penjualan
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.PenjualanViewModel
import java.io.File
import java.io.FileOutputStream

class PenjualanInvoiceFragment : BaseFragment(R.layout.fragment_penjualan_invoice) {

    private val viewModel: PenjualanViewModel by viewModels {
        PenjualanViewModel.Factory(
            requireActivity().application
        )
    }
    private lateinit var binding: FragmentPenjualanInvoiceBinding
    private var adapter: PenjualanInvoiceAdapter? = null
    private var code: String? = null
    private var printing: Printing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = arguments?.getInt("id")
        code = arguments?.getString("code")

        if (id == null || code == null) {
            findNavController().navigateUp()
            return
        }
        viewModel.setInvoiceData(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPenjualanInvoiceBinding.inflate(layoutInflater)
        initListeners()
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Printooth.init(this@PenjualanInvoiceFragment.requireContext())
        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = "Invoice Receipt Detail"
        initListeners()

        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()

        adapter = PenjualanInvoiceAdapter()

        binding.rvItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItems.adapter = this.adapter

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

        viewModel.observablePenjualan.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            bindInvoiceView(it)
        }

        binding.btnPrintInvoice.setOnClickListener {
            saveAsImage()
        }
    }

    private fun bindInvoiceView(penjualan: Penjualan) {
        binding.tvInvoiceUser.text = penjualan.karyawan?.name
        binding.tvInvoiceDate.text = penjualan.date.toAppDateFormat()
        binding.tvInvoiceNumber.text = "${code.toStringOrEmpty()}-${penjualan.id}"
        binding.tvCustName.text = penjualan.customerName
        binding.tvTotalPrice.text = penjualan.totalPrice.toIDRFormat()
        adapter?.submitList(penjualan.items)
    }

    private fun saveAsImage() {
        try {
            val filename = viewModel.getFilename(code.toStringOrEmpty())
            binding.layoutInvoice.isDrawingCacheEnabled = true;
            val bitmap = binding.layoutInvoice.drawingCache
            val f: File?
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            // Membuat latar belakang putih
            val whiteBitmap =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            whiteBitmap.eraseColor(Color.WHITE)

            // Menggabungkan gambar dengan latar belakang putih
            val canvas = Canvas(whiteBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)

            if (!path.exists()) {
                path.mkdirs()
            }

            f = File(path.absolutePath + "/" + filename + ".png")
            val ostream = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream)
            ostream.close()

            showAlertDialog(
                "Success",
                "File saved in ${f.absolutePath}",
                "OK",
                null
            )

        } catch (e: Exception) {
            showAlertDialog(
                "Save Failed",
                "Failed to save invoice image, make sure you set permission to save file",
                "OK",
                null
            )
            e.printStackTrace()
        }
    }

    private fun initListeners() {
        binding!!.btnPrint.setOnClickListener {

            if (!Printooth.hasPairedPrinter())
                resultLauncher.launch(
                    Intent(
                        this@PenjualanInvoiceFragment.requireContext(),
                        ScanningActivity::class.java
                    ),
                )
            else printDetails()
        }

        /* callback from printooth to get printer process */
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(
                    this@PenjualanInvoiceFragment.requireContext(),
                    "Connecting with printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(
                    this@PenjualanInvoiceFragment.requireContext(),
                    "Order sent to printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(
                    this@PenjualanInvoiceFragment.requireContext(),
                    "Failed to connect printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: String) {
                Toast.makeText(
                    this@PenjualanInvoiceFragment.requireContext(),
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(
                    this@PenjualanInvoiceFragment.requireContext(),
                    "Message: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun disconnected() {
                Toast.makeText(
                    this@PenjualanInvoiceFragment.requireContext(),
                    "Disconnected Printer",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode
        add(
            TextPrintable.Builder()
                .setText("Coop-Mart")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText("Kasir:      " + binding.tvInvoiceUser.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText("Tanggal:    " + binding.tvInvoiceDate.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText("Nomor Nota: " + binding.tvInvoiceNumber.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText("Pelanggan:  " + binding.tvCustName.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        val headerPrintable = TextPrintable.Builder()
            .setText("Name          Price     Quantity")
            .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setNewLinesAfter(1)
            .build()
        add(headerPrintable)

        add(
            TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )

        val items = adapter?.currentList
        items?.forEach { item ->
            val itemName = item.product?.name ?: "Unknown Product"
            val itemPrice = item.product?.sellPrice?.toIDRFormat()
            val itemQuantity = item.quantity.toString()
            val itemPrintable = TextPrintable.Builder()
                .setText("$itemName")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()
            val itemPriceQuanty = TextPrintable.Builder()
                .setText("          $itemPrice        " + "$itemQuantity")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setNewLinesAfter(1)
                .build()

            add(itemPrintable)
            add(itemPriceQuanty)
        }

        add(
            TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText("Total Harga          " + binding.tvTotalPrice.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .build()
        )
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER && result.resultCode == Activity.RESULT_OK) {
                printDetails()
            }
        }
}