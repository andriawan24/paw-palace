package com.andriawan24.pawpalace.features.booking.presenters

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.databinding.FragmentBookingConfirmationBinding
import com.andriawan24.pawpalace.features.booking.viewmodels.BookingConfirmationVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Currency
import java.util.Locale
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class BookingConfirmationFragment :
    BaseFragment<FragmentBookingConfirmationBinding, BookingConfirmationVM>() {

    private val args: BookingConfirmationFragmentArgs by navArgs()
    override val viewModel: BookingConfirmationVM by viewModels()
    override val binding: FragmentBookingConfirmationBinding by lazy {
        FragmentBookingConfirmationBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initUI()
        binding.buttonCancel.setOnClickListener {
            findNavController().navigate(BookingConfirmationFragmentDirections.actionBookingConfirmationFragmentToDetailPetShopFragment())
        }

        binding.buttonPrevious.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            viewModel.saveBooking(args.booking)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        val days = TimeUnit.MILLISECONDS.toDays(args.booking.endDate.time - args.booking.startDate.time) + 1
        binding.textViewTotalDays.text = "$days Days"
        val simpleDateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.textViewDate.text = "${simpleDateFormatter.format(args.booking.startDate)} - ${simpleDateFormatter.format(args.booking.endDate)}"
        val totalPrice = args.booking.petShop.dailyPrice * days
        val numberFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        binding.textViewPricePerDay.text = numberFormatter.format(args.booking.petShop.dailyPrice)
        binding.textViewTotalPrice.text = numberFormatter.format(totalPrice)
        binding.textViewDescription.text = args.booking.description
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.isBookingLoading.collectLatest {
                binding.buttonSave.isEnabled = !it
                binding.buttonPrevious.isEnabled = !it
                binding.buttonCancel.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.isBookingError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.isBookingSuccess.collectLatest {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                val days = TimeUnit.MILLISECONDS.toDays(args.booking.endDate.time - args.booking.startDate.time) + 1
                val simpleDateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val totalPrice = args.booking.petShop.dailyPrice * days
                val numberFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                val petShop = ChatModel.PetShop(
                    id = args.booking.petShop.id,
                    userId = args.booking.petShop.userId,
                    name = args.booking.petShop.name
                )

                findNavController().navigate(BookingConfirmationFragmentDirections.actionBookingConfirmationFragmentToChatDetailFragment(
                    petShop = petShop,
                    defaultMessage = """
                        Summary Booking
                        -------------------------------
                        Total Days: $days days and start on ${simpleDateFormatter.format(args.booking.startDate)}
                        Total Price: ${numberFormatter.format(totalPrice)}
                        
                        Notes: ${args.booking.description}
                    """.trimIndent()
                ))
            }
        }

        lifecycleScope.launch {
            viewModel.navigateToOnboarding.collectLatest {
                findNavController().navigate(R.id.action_global_onboarding)
            }
        }
    }
}