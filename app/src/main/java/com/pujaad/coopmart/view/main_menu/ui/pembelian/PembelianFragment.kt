package com.pujaad.coopmart.view.main_menu.ui.pembelian

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
import com.pujaad.coopmart.databinding.FragmentPembelianBinding
import com.pujaad.coopmart.extension.cancelLoading
import com.pujaad.coopmart.extension.showLoading
import com.pujaad.coopmart.model.Pembelian
import com.pujaad.coopmart.view.main_menu.MainMenuActivity
import com.pujaad.coopmart.viewmodel.PembelianViewModel

class PembelianFragment : BaseFragment(R.layout.fragment_pembelian) {

    private val viewModel: PembelianViewModel by viewModels { PembelianViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentPembelianBinding
    private var pembelianAdapter: PembelianAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPembelianBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lookupPembelian(null)
        pembelianAdapter = PembelianAdapter()
        pembelianAdapter?.listener = object : AdapterListener<Pembelian> {
            override fun onclick(item: Pembelian) {
                findNavController().navigate(PembelianFragmentDirections.pembelianToTransactionView(pembelian = item))
            }

        }

        binding.rvPembelian.layoutManager = LinearLayoutManager(context)
        binding.rvPembelian.adapter = pembelianAdapter

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
                    pembelianAdapter?.submitList(arrayListOf())
                    setEmptyState(true)
                }

                else -> Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.observablePembelians.observe(viewLifecycleOwner) {
            if (it.status == ResourceState.SUCCESS) {
                setEmptyState(it.data?.isEmpty() ?: false)
                pembelianAdapter?.submitList(it.data)
            }
        }

        binding.etSearchPembelian.addTextChangedListener {
            viewModel.searchHandler.postDelayed({
                searchByUserQuery()
            }, viewModel.TYPING_DELAY)
        }

        binding.btnSearch.setOnClickListener {
            searchByUserQuery()
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(PembelianFragmentDirections.pembelianToTransactionAdd())
        }
    }

    private fun searchByUserQuery() {
        val invoiceId = binding.etSearchPembelian.text.toString()
        viewModel.lookupPembelian(invoiceId)
    }

    private fun setEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNotFound.visibility = View.VISIBLE
            return
        }

        binding.tvNotFound.visibility = View.GONE
    }
}