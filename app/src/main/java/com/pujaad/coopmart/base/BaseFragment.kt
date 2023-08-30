package com.pujaad.coopmart.base

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.course.sinteraopname.base.ImageViewListener
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.view.dialog.LoadingDialog

open class BaseFragment(layoutId: Int): Fragment(layoutId), View.OnClickListener {

    var loadingDialog = LoadingDialog()
    var onRightButtonListener: ImageViewListener? = null
    var onSubmitButtonListener: ImageViewListener? = null

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.iv_action_toolbar -> Navigation.findNavController(view).navigateUp()
        }
    }

    open fun showLogoutMessage(isForceLogout: Boolean = true): String {
        return if (isForceLogout) getString(R.string.message_unauthorized)
            else getString(R.string.message_logout)
    }

    open fun setBackButton(isVisible: Boolean = false) {
        view?.let {
            val backBtn = it.findViewById<ImageView>(R.id.iv_action_toolbar)
            if (isVisible) {
                backBtn.visibility = View.VISIBLE
                backBtn.setOnClickListener(this)
            } else backBtn.visibility = View.GONE
        }
    }

    open fun setTitle(title: String) {
        view?.let {
            val toolbarTitle = it.findViewById<TextView>(R.id.tv_toolbar_title)
            toolbarTitle.text = title
        }
    }

    open fun setRightButton(isVisible: Boolean = false) {
        view?.let {
            val rightButton = it.findViewById<ImageView>(R.id.iv_action_end_toolbar)
            if (isVisible) {
                rightButton.visibility = View.VISIBLE
                rightButton.setOnClickListener(this)
            } else {
                rightButton.visibility = View.GONE
            }

            rightButton.setOnClickListener {
                onRightButtonListener?.onclick()
            }
        }
    }

    open fun getUserType(): Int {
        val pref = Prefs(requireContext())
        return try {
            pref.type?.toInt() ?: 0
        }
        catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

}