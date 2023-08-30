package com.pujaad.coopmart.helper

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class PermissionHelper {
    companion object {
        fun startPermissionRequest(
            fragment: Fragment,
            manifest: String,
            permissionInterface: PermissionInterface
        ) {
            val requestPermissionLauncher: ActivityResultLauncher<String> =
                fragment.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    permissionInterface.onGranted(isGranted)
                }

            requestPermissionLauncher.launch(manifest)
        }
    }
}

interface PermissionInterface {
    fun onGranted(isGranted: Boolean)
}

