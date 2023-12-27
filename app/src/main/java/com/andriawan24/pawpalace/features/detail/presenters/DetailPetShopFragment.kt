package com.andriawan24.pawpalace.features.detail.presenters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.databinding.FragmentDetailPetShopBinding
import com.andriawan24.pawpalace.features.detail.viewmodels.DetailPetShopVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPetShopFragment: Fragment() {

    private val viewModel: DetailPetShopVM by viewModels()
    private val args: DetailPetShopFragmentArgs by navArgs()
    private val binding: FragmentDetailPetShopBinding by lazy {
        FragmentDetailPetShopBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initUI()
        initClickListener()
        viewModel.initData(args.petShopId)
    }

    private fun initClickListener() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initObserver() {

    }

    private fun initUI() {

    }
}