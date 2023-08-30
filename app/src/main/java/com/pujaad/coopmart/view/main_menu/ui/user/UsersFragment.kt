package com.pujaad.coopmart.view.main_menu.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.course.sinteraopname.base.AdapterListener
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.common.ErrorType
import com.pujaad.coopmart.api.common.ResourceState
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentUsersBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.UsersViewModel


class UsersFragment : BaseFragment(R.layout.fragment_users) {

    private val viewModel: UsersViewModel by viewModels {
        UsersViewModel.Factory(
            requireActivity().application
        )
    }
    private lateinit var binding: FragmentUsersBinding
    private var userAdapter: UserAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentUsersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lookupUser("")
        userAdapter = UserAdapter()
        userAdapter?.listener = object : AdapterListener<Karyawan> {
            override fun onclick(item: Karyawan) {
                findNavController().navigate(UsersFragmentDirections.usersToUserTransactionView(karyawan = item))
            }

        }

        binding.rvUser.layoutManager = LinearLayoutManager(context)
        binding.rvUser.adapter = userAdapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                ErrorType.USER_NOT_FOUND -> {
                    userAdapter?.submitList(arrayListOf())
                    setEmptyState(true)
                }

                else -> Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.observableUsers.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                setEmptyState(it.data?.isEmpty() ?: false)
                userAdapter?.submitList(it.data)
            }
        }

        binding.etSearchUser.addTextChangedListener {
            viewModel.searchHandler.postDelayed({
                searchByUserQuery()
            }, viewModel.TYPING_DELAY)
        }

        binding.btnSearch.setOnClickListener {
            searchByUserQuery()
        }

        if (getUserType() == 1) {
            binding.fabAdd.visibility = View.GONE
            return
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(UsersFragmentDirections.usersToUserTransactionAdd())
        }
    }

    private fun searchByUserQuery() {
        val query = binding.etSearchUser.text.toString()
        viewModel.lookupUser(query)
    }

    private fun setEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNotFound.visibility = View.VISIBLE
            return
        }

        binding.tvNotFound.visibility = View.GONE
    }
}