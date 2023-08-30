package com.pujaad.coopmart.view.main_menu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pujaad.coopmart.R
import com.pujaad.coopmart.base.BaseFragment
import com.pujaad.coopmart.databinding.FragmentHomeBinding
import com.pujaad.coopmart.view.dialog.MainFabCallback
import com.pujaad.coopmart.view.main_menu.MainMenuActivity

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observableUsername.observe(viewLifecycleOwner) {
            if (it.isEmpty()) return@observe
            binding.userName.text = it
        }

        (requireActivity() as MainMenuActivity).fabListener = object : MainFabCallback {
            override fun onButtonClicked() {
                findNavController().navigate(HomeFragmentDirections.homeToAddPenjualan())
            }
        }
    }
}