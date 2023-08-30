package com.pujaad.coopmart.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.pujaad.coopmart.base.BaseDialogFragment
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.view.dialog.AlertCallback
import com.pujaad.coopmart.view.dialog.AlertDialog
import com.pujaad.coopmart.view.dialog.LoadingDialog

fun BaseFragment.showLoading() {
    val transaction = childFragmentManager.beginTransaction()
    val dialog = childFragmentManager.findFragmentByTag("Loading")
    if (dialog?.isAdded == true) return
    loadingDialog = LoadingDialog()
    loadingDialog.isCancelable = false
    try {
        loadingDialog.show(transaction, "Loading")
    } catch (e: IllegalStateException) { e.printStackTrace() }
}

fun BaseFragment.dismissLoading() {
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
        loadingDialog.dialog != null &&
        loadingDialog.dialog!!.isShowing &&
        !loadingDialog.isRemoving) loadingDialog.dismiss()
}

fun BaseFragment.cancelLoading() {
    if (loadingDialog.dialog != null)
        loadingDialog.dismiss()
}

fun BaseDialogFragment.showLoading() {
    val transaction = childFragmentManager.beginTransaction()
    val dialog = childFragmentManager.findFragmentByTag("Loading")
    if (dialog?.isAdded == true) return
    loadingDialog = LoadingDialog()
    loadingDialog.isCancelable = false
    try {
        loadingDialog.show(transaction, "Loading")
    } catch (e: IllegalStateException) { e.printStackTrace() }
}

fun BaseDialogFragment.dismissLoading() {
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
        loadingDialog.dialog != null &&
        loadingDialog.dialog!!.isShowing &&
        !loadingDialog.isRemoving) loadingDialog.dismiss()
}

fun Fragment.showAlertDialog(
    title: String,
    message: String,
    button: String,
    listener: AlertCallback? = null
) {
    val dialog = AlertDialog.newInstance(title, message, button)
    listener?.let { dialog.dismissListener = it }
    dialog.isCancelable = false
    dialog.show(childFragmentManager, "AlertDialog")
}