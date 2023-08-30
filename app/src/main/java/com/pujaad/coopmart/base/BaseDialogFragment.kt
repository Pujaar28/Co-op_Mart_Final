package com.pujaad.coopmart.base

import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pujaad.coopmart.R
import com.pujaad.coopmart.view.dialog.LoadingDialog

open class BaseDialogFragment() : BottomSheetDialogFragment(), View.OnClickListener {

    var loadingDialog = LoadingDialog()
    open fun showLogoutMessage(isForceLogout: Boolean = true): String {
        return if (isForceLogout) getString(R.string.message_unauthorized)
        else getString(R.string.message_logout)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.iv_close_dialog -> findNavController().navigateUp()
        }
    }

    open fun setCloseButton() {
        view?.let {
            val closeBtn = it.findViewById<ImageView>(R.id.iv_close_dialog)
            closeBtn.setOnClickListener(this)
        }
    }


}