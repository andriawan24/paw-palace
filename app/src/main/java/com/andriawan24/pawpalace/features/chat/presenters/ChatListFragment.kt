package com.andriawan24.pawpalace.features.chat.presenters

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.adapters.ChatListAdapter
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.databinding.FragmentChatListBinding
import com.andriawan24.pawpalace.features.chat.viewmodels.ChatListVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding, ChatListVM>(),
    ChatListAdapter.OnClickListener {

    private val adapter = ChatListAdapter(this)
    override val viewModel: ChatListVM by viewModels()
    override val binding: FragmentChatListBinding by lazy {
        FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initUI()
        viewModel.initData()
    }

    private fun initUI() {
        binding.recyclerViewChats.adapter = adapter
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.navigateToOnboarding.collectLatest {
                findNavController().navigate(R.id.action_global_onboarding)
            }
        }

        lifecycleScope.launch {
            viewModel.chats.collectLatest {
                val chats = mutableListOf<Pair<ChatModel.PetShop, ChatModel>>()
                it.keys.forEach { key ->
                    it[key]?.first { lastChat ->
                        chats.add(Pair(key, lastChat))
                    }
                }
                adapter.setItem(chats)
            }
        }
    }

    override fun onChatClicked(petShop: ChatModel.PetShop) {
        findNavController().navigate(ChatListFragmentDirections.actionChatFragmentToChatDetailFragment(petShop))
    }
}