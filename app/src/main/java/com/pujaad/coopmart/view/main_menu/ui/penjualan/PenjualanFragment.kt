package com.pujaad.coopmart.view.main_menu.ui.penjualan

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
import com.pujaad.coopmart.databinding.FragmentPenjualanBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.model.Penjualan
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.PenjualanViewModel

class PenjualanFragment : BaseFragment(R.layout.fragment_penjualan) {

    private val viewModel: PenjualanViewModel by viewModels { PenjualanViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentPenjualanBinding
    private var adapter: PenjualanAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPenjualanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lookupPenjualan(null)
        adapter = PenjualanAdapter()
        adapter?.listener = object : AdapterListener<Penjualan> {
            override fun onclick(item: Penjualan) {
                findNavController().navigate(PenjualanFragmentDirections.penjualanToTransactionView(penjualan = item))
            }

        }

        binding.rvPenjualan.layoutManager = LinearLayoutManager(context)
        binding.rvPenjualan.adapter = this.adapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) showLoading() else cancelLoading()
        }

        viewModel.onError.observe(viewLifecycleOwner) {
            when (it.type) {
                ErrorType.LOGIN_UNAUTHORIZED -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainMenuActivity).logout()
                }

                ErrorType.INVENTORY_NOT_FOUND -> {
                    adapter?.submitList(arrayListOf())
                    setEmptyState(true)
                }

                else -> Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.observablePenjualans.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                setEmptyState(it.data?.isEmpty() ?: false)
                adapter?.submitList(it.data)
            }
        }

        binding.etSearchPenjualan.addTextChangedListener {
            viewModel.searchHandler.postDelayed({
                searchByUserQuery()
            }, viewModel.TYPING_DELAY)
        }

        binding.btnSearch.setOnClickListener {
            searchByUserQuery()
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(PenjualanFragmentDirections.penjualanToTransactionAdd())
        }
    }

    private fun searchByUserQuery() {
        val invoiceId = binding.etSearchPenjualan.text.toString()
        viewModel.lookupPenjualan(invoiceId)
    }

    private fun setEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNotFound.visibility = View.VISIBLE
            return
        }

        binding.tvNotFound.visibility = View.GONE
    }

}