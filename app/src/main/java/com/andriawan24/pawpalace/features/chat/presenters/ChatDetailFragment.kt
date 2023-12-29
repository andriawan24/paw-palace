package com.andriawan24.pawpalace.features.chat.presenters

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.adapters.ChatItemAdapter
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.databinding.FragmentChatDetailBinding
import com.andriawan24.pawpalace.features.chat.viewmodels.ChatDetailVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatDetailFragment : BaseFragment<FragmentChatDetailBinding, ChatDetailVM>() {

    private val args: ChatDetailFragmentArgs by navArgs()
    private val adapter = ChatItemAdapter()
    override val viewModel: ChatDetailVM by viewModels()
    override val binding: FragmentChatDetailBinding by lazy {
        FragmentChatDetailBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initPetShop(args.petShop)
        binding.imageViewBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonSend.setOnClickListener {
            viewModel.sendMessage(binding.textInputMessage.text.toString(), petShop = args.petShop)
            binding.textInputMessage.setText("")
        }
        binding.recyclerViewChat.adapter = adapter
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewModel.initData(args.petShop.id)
        if (args.defaultMessage != null) {
            viewModel.sendMessage(args.defaultMessage.orEmpty(), args.petShop)
            viewModel.sendMessage("Teknis Pembayarannya itu bagaimana yaa kak?", args.petShop)
        }
    }

    private fun initPetShop(petShop: ChatModel.PetShop) {
        binding.textViewName.text = petShop.name
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.chats.collectLatest {
                val anyList =  mutableListOf<Any>()

                it.keys.reversed().forEach { date ->
                    anyList.add(date)
                    it[date]?.reversed()?.forEach { chat ->
                        anyList.add(chat)
                    }
                }

                adapter.setData(anyList)
                binding.recyclerViewChat.scrollToPosition(anyList.lastIndex)
            }
        }

        lifecycleScope.launch {
            viewModel.navigateToOnboarding.collectLatest {
                findNavController().navigate(R.id.action_global_onboarding)
            }
        }
    }
}