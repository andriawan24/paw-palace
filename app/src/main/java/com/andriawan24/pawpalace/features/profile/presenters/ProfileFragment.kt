package com.andriawan24.pawpalace.features.profile.presenters

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.FragmentProfileBinding
import com.andriawan24.pawpalace.features.profile.viewmodels.ProfileVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileVM by viewModels()
    private val binding: FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
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
        initClickListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.initData()
    }

    private fun initClickListener() {
        binding.buttonLogout.setOnClickListener {
            viewModel.signOut()
        }

        binding.buttonEditProfile.setOnClickListener {
            viewModel.setPetOwnerMode.value?.first?.let { user ->
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToEditProfilePetOwnerFragment(user)
                )
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.isProfileLoading.collectLatest {
                binding.buttonLogout.isEnabled = !it
                binding.buttonEditProfile.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.navigateToOnboarding.collectLatest {
                findNavController().navigate(R.id.action_global_onboarding)
            }
        }

        lifecycleScope.launch {
            viewModel.setPetOwnerMode.collectLatest {
                it?.first?.let { user ->
                    setupPetOwner(user, it.second)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.setPetShopMode.collectLatest {
                binding.textViewName.text = it.first.second.name
                binding.textViewEmail.text = it.first.first?.email.orEmpty()
                it.second?.let { imageUri ->
                    binding.imageViewProfile.setImageURI(imageUri)
                }
            }
        }
    }

    private fun setupPetOwner(petOwner: UserModel, imageUri: Uri?) {
        binding.textViewName.text = petOwner.name
        binding.textViewEmail.text = petOwner.email
        imageUri?.let {
            binding.imageViewProfile.setImageURI(it)
        }
    }
}