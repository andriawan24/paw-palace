package com.andriawan24.pawpalace.features.home.presenters


import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View.GONE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.adapters.PetShopItemAdapter
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.databinding.FragmentHomeBinding
import com.andriawan24.pawpalace.features.home.viewmodels.HomeVM
import com.andriawan24.pawpalace.utils.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeVM>(), PetShopItemAdapter.OnClickListener {

    private val adapter = PetShopItemAdapter(this)
    override val viewModel: HomeVM by viewModels()
    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        binding.recyclerViewPetShop.adapter = adapter
        binding.recyclerViewPetShop.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(GridSpacingItemDecoration(2, 20))
        }
        viewModel.initData()
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.setPetShopMode.collectLatest {
                setupPetShopMode(petShop = it)
            }
        }

        lifecycleScope.launch {
            viewModel.setPetOwnerMode.collectLatest {
                setupPetOwnerMode(location = it.location)
            }
        }

        lifecycleScope.launch {
            viewModel.getPetShopsLoading.collectLatest {
                // do something when loading
            }
        }

        lifecycleScope.launch {
            viewModel.getPetShopsError.collectLatest { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.getPetShopsSuccess.collectLatest {
                Timber.d("Pet Shops: $it")
                binding.textViewPetShopCount.text = "Showing ${it.size} Pet Shop"
                adapter.setData(it)
            }
        }
    }

    private fun setupPetShopMode(petShop: PetShopModel) {
        val title = "Hi, ${petShop.name}"
        val titleToChange = petShop.name
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

        binding.textViewLocation.text = petShop.location.ifEmpty { "Location not set" }
        binding.textViewPetShopCount.text = getString(R.string.showing_your_slot)
        binding.textInputLayoutSearch.visibility = GONE
        binding.buttonSearch.visibility = GONE
    }

    private fun setupPetOwnerMode(location: String) {
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
        binding.textViewLocation.text = location.ifEmpty { "Location not set" }
    }

    override fun onDetailClicked(id: String) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDetailPetShopFragment(id)
        )
    }

    override fun onChatClicked(id: String) {
        // Do something
    }
}