package com.pujaad.coopmart.view.login

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.common.ResourceState
import com.pujaad.coopmart.databinding.ActivityAwalBinding
import com.pujaad.coopmart.ui.BarcodeScanningActivity
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.AwalViewModel

class Awal : AppCompatActivity() {

    private lateinit var loading: ProgressDialog
    private val cameraPermissionRequestCode = 1
    private var selectedScanningSDK = BarcodeScanningActivity.ScannerSDK.MLKIT
    private val viewModel: AwalViewModel by viewModels { AwalViewModel.Factory(application) }
    private lateinit var binding: ActivityAwalBinding
    var REQ_PERMISSION = 101
    private var doubleBackToExitPressedOnce = false
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = ProgressDialog(this)
        setPermission()
        binding = ActivityAwalBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        prefs = Prefs(this)
        if (!prefs.id.isNullOrEmpty()
            && !prefs.user.isNullOrEmpty()
            && !prefs.type.isNullOrEmpty()
        ) {
            startActivity(Intent(applicationContext, MainMenuActivity::class.java))
            finish()
        }


        binding.btnLogin.setOnClickListener {
            viewModel.login(
                username = binding.tieEmail.text.toString(),
                password = binding.tiePassword.text.toString(),
            )
//            selectedScanningSDK = BarcodeScanningActivity.ScannerSDK.MLKIT
//            startScanning()
        }

        viewModel.observableUser.observe(this) {
            if (it.status == ResourceState.SUCCESS) {
                startActivity(Intent(this@Awal, MainMenuActivity::class.java))
                finish()
            }
        }

        viewModel.isLoading.observe(this) {
            if (it) loading.show() else loading.dismiss()
        }

        viewModel.onError.observe(this) {
            Toast.makeText(this@Awal, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN)!= PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_SCAN)!= PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)!= PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQ_PERMISSION
            )
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraWithScanner()
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityForResult(intent, cameraPermissionRequestCode)
            }
        }
    }
    private fun resetPassword(){

    }

    private fun openCameraWithScanner() {
        BarcodeScanningActivity.start(this, selectedScanningSDK)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraPermissionRequestCode) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCameraWithScanner()
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Tekan tombol Back untuk keluar", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

}