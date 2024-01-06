package com.andriawan24.pawpalace.features.chat.presenters

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.adapters.ChatListAdapter
import com.andriawan24.pawpalace.adapters.ChatListPetShopAdapter
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.FragmentChatListBinding
import com.andriawan24.pawpalace.features.chat.viewmodels.ChatListVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding, ChatListVM>(),
    ChatListAdapter.OnClickListener, ChatListPetShopAdapter.OnClickListener {

    private val adapter = ChatListAdapter(this)
    private val adapterPetShop = ChatListPetShopAdapter(this)
    override val viewModel: ChatListVM by viewModels()
    override val binding: FragmentChatListBinding by lazy {
        FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        viewModel.initData()
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.setPetOwner.collectLatest {
                binding.recyclerViewChats.adapter = adapter
                binding.recyclerViewChats.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        lifecycleScope.launch {
            viewModel.setPetShop.collectLatest {
                binding.recyclerViewChats.adapter = adapterPetShop
                binding.recyclerViewChats.layoutManager = LinearLayoutManager(requireContext())
            }
        }

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

        lifecycleScope.launch {
            viewModel.chatsPetShop.collectLatest {
                val chats = mutableListOf<Pair<UserModel, ChatModel>>()
                it.keys.forEach { key ->
                    it[key]?.first { lastChat ->
                        chats.add(Pair(key, lastChat))
                    }
                }
                adapterPetShop.setItem(chats)
            }
        }
    }

    override fun onChatClicked(petShop: ChatModel.PetShop) {
        findNavController().navigate(ChatListFragmentDirections.actionChatFragmentToChatDetailFragment(
            petShop = petShop
        ))
    }

    override fun onChatPetShopClicked(chat: ChatModel) {
        findNavController().navigate(ChatListFragmentDirections.actionChatFragmentToChatDetailFragment(
            petOwner = chat.sender,
            petShop = chat.petShop
        ))
    }
}