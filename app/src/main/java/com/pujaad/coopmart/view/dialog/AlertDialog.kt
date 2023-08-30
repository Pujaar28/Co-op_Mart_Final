package com.pujaad.coopmart.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pujaad.coopmart.databinding.FragmentAlertDialogBinding

class AlertDialog : DialogFragment() {
    private var mTitle: String = ""
    private var mMessage: String = ""
    private var mButton: String = ""
    private lateinit var binding: FragmentAlertDialogBinding
    var dismissListener: AlertCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
        arguments?.let {
            mTitle = it.getString(TITLE, "")
            mMessage = it.getString(MESSAGE, "")
            mButton = it.getString(BUTTON, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = mTitle
        binding.message.text = mMessage
        binding.button.text = mButton

        binding.button.setOnClickListener {
            dismissListener?.let { callback ->
                dialog?.let { callback.onButtonClicked(it) }

            } ?: run {
                dismiss()
            }
        }
    }

    companion object {
        private const val TITLE = "username"
        private const val MESSAGE = "token"
        private const val BUTTON = "button"

        fun newInstance(title: String, message: String, button: String) =
            AlertDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(MESSAGE, message)
                    putString(BUTTON, button)
                }
            }
    }

}