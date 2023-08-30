package com.pujaad.coopmart.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.pujaad.coopmart.databinding.FragmentLoadingDialogBinding

class LoadingDialog : DialogFragment() {

    lateinit var binding: FragmentLoadingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentLoadingDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        private var INSTANCE: LoadingDialog? = null

        @JvmStatic
        fun getInstance(): LoadingDialog {
            if (INSTANCE == null) {
                synchronized(LoadingDialog::javaClass) {
                    INSTANCE = LoadingDialog()
                }
            }
            return INSTANCE!!
        }

    }
}