package com.pujaad.coopmart.view.main_menu.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentChangePasswordBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.view.dialog.AlertCallback
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.ProfileViewModel

class ChangePasswordFragment : BaseFragment(R.layout.fragment_change_password) {

    private val viewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }
                else -> showAlertDialog("Error", it.message.toString(), "OK")
            }

        }

        viewModel.changePasswordSuccess.observe(viewLifecycleOwner) {
            if (it) {
                showAlertDialog("Success", "Change Password success, You will require to login after this proccess!", "OK", object : AlertCallback {
                    override fun onButtonClicked(dialog: Dialog) {
                        dialog.dismiss()
                        (requireActivity() as MainMenuActivity).logout()
                    }

                })

            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            val currentPassword = binding.tieCurrentPassword.text.toString()
            val newPassword = binding.tieNewPassword.text.toString()
            val confirmNewPassword = binding.tieConfirmNewPassword.text.toString()

            viewModel.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmNewPassword = confirmNewPassword
            )
        }
    }
}