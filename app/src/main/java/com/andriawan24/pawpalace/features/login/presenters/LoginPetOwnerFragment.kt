package com.andriawan24.pawpalace.features.login.presenters

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ParagraphStyle
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.databinding.FragmentLoginPetOwnerBinding
import com.andriawan24.pawpalace.features.login.viewmodels.LoginVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginPetOwnerFragment : Fragment() {

    private val viewModel: LoginVM by viewModels()
    private val binding: FragmentLoginPetOwnerBinding by lazy {
        FragmentLoginPetOwnerBinding.inflate(layoutInflater)
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
        initUI()
        initTextListener()
        initClickListener()
    }

    private fun initClickListener() {
        binding.buttonSignIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (validateInput(email, password)) {
                findNavController().navigate(LoginPetOwnerFragmentDirections.actionLoginPetOwnerFragmentToHomeFragment())
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (!validateEmail(email)) {
            isValid = false
        }

        if (!validatePassword(password)) {
            isValid = false
        }

        return isValid
    }

    private fun initTextListener() {
        binding.editTextEmail.doAfterTextChanged { validateEmail(it.toString()) }
        binding.editTextPassword.doAfterTextChanged { validatePassword(it.toString()) }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.editTextLayoutEmail.isErrorEnabled = true
            binding.editTextLayoutEmail.error = "Email is empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextLayoutEmail.isErrorEnabled = true
            binding.editTextLayoutEmail.error = "Email is not valid"
            return false
        }

        binding.editTextLayoutEmail.error = null
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            binding.editTextLayoutPassword.isErrorEnabled = true
            binding.editTextLayoutPassword.error = "Password is empty"
            return false
        }

        binding.editTextLayoutPassword.error = null
        return true
    }

    private fun initUI() {
        var title = getString(R.string.sign_in_pet_owner_title)
        var titleToChange = "Pet’s Owner"
        var spannable = SpannableString(title)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textViewSignInPetOwnerTitle.text = spannable

        title = getString(R.string.do_not_have_account_title)
        titleToChange = "Sign Up"
        spannable = SpannableString(title)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(view: View) {
                    findNavController().navigate(LoginPetOwnerFragmentDirections.actionLoginPetOwnerFragmentToRegisterPetOwnerFragment())
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            },
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(BOLD),
            title.indexOf(titleToChange),
            title.count(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textViewRegister.text = spannable
        binding.textViewRegister.movementMethod = LinkMovementMethod()
    }
}