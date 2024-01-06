package com.andriawan24.pawpalace.features.home.presenters


import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View.GONE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.adapters.PetShopItemAdapter
import com.andriawan24.pawpalace.adapters.SlotItemAdapter
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.FragmentHomeBinding
import com.andriawan24.pawpalace.features.home.viewmodels.HomeVM
import com.andriawan24.pawpalace.utils.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeVM>(),
    PetShopItemAdapter.OnClickListener, SlotItemAdapter.OnClickListener {

    private val petShopAdapter = PetShopItemAdapter(this)
    private val slotItemAdapter = SlotItemAdapter(this)
    override val viewModel: HomeVM by viewModels()
    override val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private var currentLocation = ""

    override fun onInitViews() {
        binding.textViewLocation.text = getString(R.string.location_not_set)
        setFragmentResultListener(FragmentSetLocationDialog.SET_LOCATION_REQUEST_KEY) { _, bundle ->
            val location = bundle.getString(FragmentSetLocationDialog.SET_LOCATION_RESULT_KEY).orEmpty()
            currentLocation = location
            binding.textViewLocation.text = currentLocation.ifEmpty { getString(R.string.location_not_set) }
            viewModel.getPetShops(location = currentLocation, query = binding.textInputEditTextSearch.text.toString())
        }

        binding.buttonChangeLocation.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFragmentSetLocationDialog(
                    currentLocation
                )
            )
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.getPetShops(
                location = currentLocation,
                query = binding.textInputEditTextSearch.text.toString()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.initData()
    }

    @SuppressLint("SetTextI18n")
    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.setPetShopMode.collectLatest {
                binding.recyclerViewPetShop.adapter = slotItemAdapter
                binding.recyclerViewPetShop.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    addItemDecoration(GridSpacingItemDecoration(2, 20))
                }
                setupPetShopMode(petShop = it)
                viewModel.getPetOwners(it)
            }
        }

        lifecycleScope.launch {
            viewModel.setPetOwnerMode.collectLatest {
                binding.recyclerViewPetShop.adapter = petShopAdapter
                binding.recyclerViewPetShop.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    addItemDecoration(GridSpacingItemDecoration(2, 20))
                }
                setupPetOwnerMode(location = it.location)
                viewModel.getPetShops()
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
                binding.textViewPetShopCount.text = "Showing ${it.size} Pet Shop"
                petShopAdapter.setData(it)
            }
        }

        lifecycleScope.launch {
            viewModel.getPetOwnerLoading.collectLatest {
                // do something when loading
            }
        }

        lifecycleScope.launch {
            viewModel.getPetOwnerError.collectLatest { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.getPetOwnerSuccess.collectLatest {
                val bookings = mutableListOf<BookingModel?>()
                (1..it.second).forEach { _ ->
                    bookings.add(null)
                }
                it.first.indices.forEach { bookingIndex ->
                    bookings[bookingIndex] = it.first[bookingIndex]
                }
                slotItemAdapter.setData(bookings)
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

        binding.buttonChangeLocation.visibility = GONE
        binding.textViewLocation.text =
            petShop.location.ifEmpty { getString(R.string.location_not_set) }
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
    }

    override fun onDetailClicked(id: String) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDetailPetShopFragment(id)
        )
    }

    override fun onChatClicked(petShop: PetShopModel) {
        val petShopChat = ChatModel.PetShop(
            id = petShop.id,
            userId = petShop.userId,
            name = petShop.name
        )

        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToChatDetailFragment(
                petShop = petShopChat
            )
        )
    }

    override fun onDetailOrderClickedClicked(booking: BookingModel) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToHistoryDetailDialogFragment(
                booking,
                isPetShop = true
            )
        )
    }

    override fun onChatClicked(petOwner: UserModel, petShopModel: PetShopModel) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToChatDetailFragment(
                petOwner = petOwner,
                petShop = ChatModel.PetShop(
                    petShopModel.id,
                    petShopModel.userId,
                    petShopModel.name
                )
            )
        )
    }
}