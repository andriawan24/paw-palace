package com.andriawan24.pawpalace.features.home.presenters


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.databinding.FragmentHomeBinding
import com.andriawan24.pawpalace.features.home.viewmodels.HomeVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeVM by viewModels()
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
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
        viewModel.initData()
    }

    @SuppressLint("SetTextI18n")
    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.setPetShopMode.collectLatest {
                val title = "Hi, ${it.name}"
                val titleToChange = it.name
                val spannable = SpannableString(title)
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    ),
                    title.indexOf(titleToChange),
                    title.count(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.textViewTitle.text = spannable

                binding.textViewLocation.text = it.location.ifEmpty { "Location not set" }
                binding.textViewPetShopCount.text = "Showing Your Slot"
                binding.textInputLayoutSearch.visibility = GONE
                binding.buttonSearch.visibility = GONE
            }
        }

        lifecycleScope.launch {
            viewModel.setPetOwnerMode.collectLatest {
                val title = "Find Out Place for Your Pet Here"
                val titleToChange = "Place for Your Pet"
                val spannable = SpannableString(title)
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    ),
                    title.indexOf(titleToChange),
                    title.indexOf(titleToChange) + titleToChange.count(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.textViewTitle.text = spannable

                binding.textViewLocation.text = it.location.ifEmpty { "Location not set" }
            }
        }
    }
}