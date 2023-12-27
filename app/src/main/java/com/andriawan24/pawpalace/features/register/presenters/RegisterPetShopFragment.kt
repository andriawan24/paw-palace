package com.andriawan24.pawpalace.features.register.presenters

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.databinding.FragmentRegisterPetShopBinding
import com.andriawan24.pawpalace.features.register.viewmodels.RegisterPetShopVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterPetShopFragment : Fragment() {

    private val viewModel: RegisterPetShopVM by viewModels()
    private val binding: FragmentRegisterPetShopBinding by lazy {
        FragmentRegisterPetShopBinding.inflate(layoutInflater)
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
        initTextListener()
        initClickListener()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.isRegisterLoading.collectLatest {
                binding.buttonSignUp.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.registerError.collectLatest { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.registerSuccess.collectLatest {
                findNavController().navigate(
                    RegisterPetShopFragmentDirections
                        .actionRegisterPetShopFragmentToCompleteRegisterPetShopFragment(it)
                )
            }
        }
    }

    private fun initClickListener() {
        binding.buttonSignUp.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val phoneNumber = binding.editTextPhone.text.toString()
            val password = binding.editTextPassword.text.toString()
            val passwordConfirmation = binding.editTextPasswordConfirmation.text.toString()

            if (validateInput(name, email, phoneNumber, password, passwordConfirmation)) {
                viewModel.register(
                    name = name,
                    phoneNumber = phoneNumber,
                    email = email,
                    password = password
                )
            }
        }
    }

    private fun initUI() {
        var title = getString(R.string.sign_up_pet_shop_title)
        var titleToChange = "Pet Shop"
        var spannable = SpannableString(title)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textViewSignUpPetShopTitle.text = spannable

        title = getString(R.string.already_have_account_title)
        titleToChange = "Sign In"
        spannable = SpannableString(title)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(view: View) {
                    findNavController().navigateUp()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            },
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textViewLogin.text = spannable
        binding.textViewLogin.movementMethod = LinkMovementMethod()
    }

    private fun initTextListener() {
        binding.editTextName.doAfterTextChanged { validateName(it.toString()) }
        binding.editTextEmail.doAfterTextChanged { validateEmail(it.toString()) }
        binding.editTextPhone.doAfterTextChanged { validatePhoneNumber(it.toString()) }
        binding.editTextPassword.doAfterTextChanged { validatePassword(it.toString()) }
        binding.editTextPasswordConfirmation.doAfterTextChanged {
            validatePasswordConfirmation(
                binding.editTextPassword.text.toString(),
                it.toString()
            )
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        phoneNumber: String,
        password: String,
        passwordConfirmation: String
    ): Boolean {
        var isValid = true

        if (!validateName(name)) {
            isValid = false
        }

        if (!validateEmail(email)) {
            isValid = false
        }

        if (!validatePhoneNumber(phoneNumber)) {
            isValid = false
        }

        if (!validatePassword(password)) {
            isValid = false
        }

        if (!validatePasswordConfirmation(password, passwordConfirmation)) {
            isValid = false
        }

        return isValid
    }

    private fun validateName(name: String): Boolean {
        if (name.isBlank()) {
            binding.editTextLayoutName.isErrorEnabled = true
            binding.editTextLayoutName.error = "Name cannot be empty"
            return false
        }

        binding.editTextLayoutName.error = null
        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            binding.editTextLayoutEmail.isErrorEnabled = true
            binding.editTextLayoutEmail.error = "Email cannot be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextLayoutEmail.isErrorEnabled = true
            binding.editTextLayoutEmail.error = "Email is not valid"
            return false
        }

        binding.editTextLayoutEmail.error = null
        return true
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) {
            binding.editTextLayoutPhone.isErrorEnabled = true
            binding.editTextLayoutPhone.error = "Phone number cannot be empty"
            return false
        } else if (!phoneNumber.isDigitsOnly()) {
            binding.editTextLayoutPhone.isErrorEnabled = true
            binding.editTextLayoutPhone.error = "Phone number must contains digit only"
            return false
        }

        binding.editTextLayoutPhone.error = null
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isBlank()) {
            binding.editTextLayoutPassword.isErrorEnabled = true
            binding.editTextLayoutPassword.error = "Password cannot be empty"
            return false
        } else if (password.count() !in 8..12) {
            binding.editTextLayoutPassword.isErrorEnabled = true
            binding.editTextLayoutPassword.error = "Password length must be between 8 and 12"
            return false
        }

        binding.editTextLayoutPassword.error = null
        return true
    }

    private fun validatePasswordConfirmation(
        password: String,
        passwordConfirmation: String
    ): Boolean {
        if (passwordConfirmation.isBlank()) {
            binding.editTextLayoutPasswordConfirmation.isErrorEnabled = true
            binding.editTextLayoutPasswordConfirmation.error =
                "Password confirmation cannot be empty"
            return false
        } else if (password != passwordConfirmation) {
            binding.editTextLayoutPasswordConfirmation.isErrorEnabled = true
            binding.editTextLayoutPasswordConfirmation.error =
                "Password confirmation is not the same"
            return false
        }

        binding.editTextLayoutPasswordConfirmation.error = null
        return true
    }
}