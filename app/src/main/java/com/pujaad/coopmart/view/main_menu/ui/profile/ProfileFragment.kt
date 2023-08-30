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
import com.pujaad.coopmart.api.common.ResourceState
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentProfileBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.view.dialog.AlertCallback
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_user_transaction.view.*

class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getProfile()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observableUser.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                val user = it.data ?: return@observe
                binding.tieUsername.setText(user.username)
                binding.tieName.setText(user.name)
                binding.tieAge.setText(user.age.toString())
                binding.tieRole.setText(if (user.type == 0) "Admin" else "Kasir")
                binding.tiePhone.setText(user.phone)
                binding.tieAddress.setText(user.address)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }
                else -> Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.changeProfileSuccess.observe(viewLifecycleOwner) {
            if (it) {
                showAlertDialog("Success", "Change Profile success", "OK", object : AlertCallback {
                    override fun onButtonClicked(dialog: Dialog) {
                        dialog.dismiss()
                        adjustFormState(false)
                        viewModel.getProfile()
                    }

                })

            }
        }

        binding.btnEdit.setOnClickListener {
            adjustFormState(true)
        }

        binding.btnChangePassword.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.profileToChangePassword())
        }

        binding.btnCancel.setOnClickListener {
            adjustFormState(false)
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.tieName.text.toString()
            val username = binding.tieUsername.text.toString()
            val phone = binding.tiePhone.text.toString()
            val address = binding.tieAddress.text.toString()
            val age = binding.tieAge.text.toString()
            val ageString: Int? = age.toIntOrNull()
            if (ageString != null) {
                viewModel.changeProfile(
                    name = name,
                    username = username,
                    phone = phone,
                    address = address,
                    age = ageString
                )
            }
        }
    }

    private fun adjustFormState(isEditState: Boolean) {
        if (isEditState) {
            binding.layoutStateEdit.visibility = View.VISIBLE
            binding.layoutBtnActions.visibility = View.INVISIBLE
            return
        }

        binding.layoutStateEdit.visibility = View.GONE
        binding.layoutBtnActions.visibility = View.VISIBLE

    }

}