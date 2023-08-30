package com.pujaad.coopmart.view.main_menu.ui.inventory.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.zxing.Result
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentInventoryScannerBinding
import com.pujaad.coopmart.extension.dismissLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.helper.PermissionHelper
import com.pujaad.coopmart.helper.PermissionInterface
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.InventoryViewModel
import me.dm7.barcodescanner.zxing.ZXingScannerView

class InventoryScannerFragment : BaseFragment(R.layout.fragment_inventory_scanner), ZXingScannerView.ResultHandler {

    companion object {
        const val REQUEST_KEY = "SELECTED_PRODUCT_KEY"
    }

    private val viewModel: InventoryViewModel by viewModels { InventoryViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentInventoryScannerBinding

    private lateinit var mScannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mScannerView = ZXingScannerView(context)
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventoryScannerBinding.inflate(layoutInflater)
        binding.scannerView.addView(mScannerView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = "Scan Inventory"

        PermissionHelper.startPermissionRequest(this, Manifest.permission.CAMERA, object :
            PermissionInterface {
            @SuppressLint("MissingPermission")
            override fun onGranted(isGranted: Boolean) {
                if (!isGranted) {
                    findNavController().navigateUp()
                    Toast.makeText(binding.root.context, "This feature require camera access to continue", Toast.LENGTH_SHORT).show()
                }
            }

        })

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else dismissLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                else -> {
                    mScannerView.startCamera()
                    mScannerView.setResultHandler(this)
                    Toast.makeText(requireContext(), it.message.toStringOrEmpty(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.observableProduct.observe(viewLifecycleOwner) {
            mScannerView.setResultHandler(this)
            val bundle = Bundle()
            bundle.putParcelable(Constant.SELECTED_ITEM, it)
            setFragmentResult(REQUEST_KEY, bundle)
            Navigation.findNavController(binding.root).navigateUp()
        }

    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {

        val result = rawResult.text
        try {
            val qrDataArr = result.split('-')
            val qrData = Pair(qrDataArr[0], qrDataArr[1].toInt())
            viewModel.getInventoryById(qrData.second)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid QR code detected", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            mScannerView.startCamera()
            mScannerView.setResultHandler(this)
        }
    }
}