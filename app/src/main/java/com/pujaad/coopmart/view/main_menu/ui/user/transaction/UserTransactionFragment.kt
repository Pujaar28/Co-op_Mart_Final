package com.pujaad.coopmart.view.main_menu.ui.user.transaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.Constant
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentUserTransactionBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showAlertDialog
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.view.dialog.AlertCallback
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.view.main_menu.ui.inventory.transaction.InventoryTransactionFragment
import com.pujaad.coopmart.viewmodel.UsersViewModel

class UserTransactionFragment : BaseFragment(R.layout.fragment_user_transaction) {

    companion object {
        const val REQUEST_KEY = "USER_KEY"
    }

    private val viewModel: UsersViewModel by viewModels { UsersViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentUserTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = arguments?.getParcelable<Karyawan>("karyawan")
        if (user != null) viewModel.setUser(user)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportActionBar = (activity as AppCompatActivity?)!!.supportActionBar
        supportActionBar?.title = setFormtitle()

        viewModel.observableUser.observe(viewLifecycleOwner) {
            bindForm(it)
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

                else -> showAlertDialog(
                    "Operation Failed",
                    it.message.toStringOrEmpty(),
                    "OK",
                    null
                )
            }
        }

        viewModel.onSuccessSubmit.observe(viewLifecycleOwner) {
            if (!it) return@observe

            val operationType = if (viewModel.isAddFormState) "add" else "update"
            val message = "Success $operationType user!"
            showAlertDialog("Operation Success", message, "OK", object : AlertCallback {
                override fun onButtonClicked(dialog: Dialog) {
                    val bundle = Bundle()
                    bundle.putBoolean(Constant.OPERATION_STATUS, true)
                    setFragmentResult(InventoryTransactionFragment.REQUEST_KEY, bundle)
                    findNavController().navigateUp()
                }
            })

        }

        binding.btnSave.setOnClickListener {
            val name = binding.tieName.text.toString()
            val username = binding.tieUsername.text.toString()
            val phone = binding.tiePhone.text.toString()
            val address = binding.tieAddress.text.toString()
            val ageString = binding.tieAge.text.toString()
            val age: Int? = ageString.toIntOrNull()
            viewModel.submitUser(
                name = name,
                age = age,
                username = username,
                phone = phone,
                address = address,

            )
        }

    }

    private fun bindForm(user: Karyawan) {
        if (!viewModel.isAddFormState) {
//            binding.btnSave.isEnabled = false
//            binding.tieName.isEnabled = false
//            binding.tieUsername.isEnabled = false
//            binding.tiePhone.isEnabled = false
//            binding.tieAddress.isEnabled = false
//            binding.tieAge.isEnabled = false
        }

        binding.tieName.setText(user.name)
        binding.tieUsername.setText(user.username)
        binding.tiePhone.setText(user.phone)
        binding.tieAddress.setText(user.address)
        binding.tieAge.setText(user.age.toString())
    }

    private fun setFormtitle(): String {
        return if (viewModel.isAddFormState)
            getString(R.string.tv_title_add_user)
        else
            getString(R.string.tv_title_view_user)
    }
}