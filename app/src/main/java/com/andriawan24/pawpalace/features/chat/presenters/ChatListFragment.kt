package com.andriawan24.pawpalace.features.chat.presenters

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.databinding.FragmentChatListBinding
import com.andriawan24.pawpalace.features.chat.viewmodels.ChatListVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding, ChatListVM>() {

    override val viewModel: ChatListVM by viewModels()
    override val binding: FragmentChatListBinding by lazy {
        FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        viewModel.initData()
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.navigateToOnboarding.collectLatest {
                findNavController().navigate(R.id.action_global_onboarding)
            }
        }
    }
}