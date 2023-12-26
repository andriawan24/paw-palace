package com.andriawan24.pawpalace.features.onboarding.presenters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.adapters.OnboardingItemAdapter
import com.andriawan24.pawpalace.data.models.OnboardingModel
import com.andriawan24.pawpalace.databinding.FragmentOnboardingBinding
import com.andriawan24.pawpalace.features.onboarding.viewmodels.OnboardingVM
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private val viewModel: OnboardingVM by viewModels()
    private val adapter = OnboardingItemAdapter()
    private val binding: FragmentOnboardingBinding by lazy {
        FragmentOnboardingBinding.inflate(layoutInflater)
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
        viewModel.checkUserLogin()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.navigateToHome.collectLatest {
                findNavController().navigate(OnboardingFragmentDirections.actionGlobalHome())
            }
        }
    }

    private fun initClickListener() {
        binding.buttonSignInPetOwner.setOnClickListener {
            findNavController().navigate(
                OnboardingFragmentDirections
                    .actionOnboardingFragmentToLoginFragment()
            )
        }

        binding.buttonSignInPetShop.setOnClickListener {
            findNavController().navigate(
                OnboardingFragmentDirections.actionOnboardingFragmentToLoginPetShopFragment()
            )
        }
    }

    private fun initUI() {
        binding.viewPagerOnboarding.adapter = adapter
        adapter.setData(onboardingItems)
        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerOnboarding) { _, _ ->
            // Do nothing
        }.attach()
    }

    companion object {
        private val onboardingItems = listOf(
            OnboardingModel(
                image = R.drawable.img_onboarding_1,
                title = "Welcome Pet’s Owner",
                subtitle = "With a single click, quickly find pet boarding for your beloved companions"
            ),
            OnboardingModel(
                image = R.drawable.img_onboarding_2,
                title = "Right Place For Your Pet",
                subtitle = "Your pets will be treated by animal lovers, just like you"
            ),
            OnboardingModel(
                image = R.drawable.img_onboarding_3,
                title = "Easy, Secure & Dependable",
                subtitle = "Trust is the foundation, and we're here to build it with you."
            )
        )
    }
}