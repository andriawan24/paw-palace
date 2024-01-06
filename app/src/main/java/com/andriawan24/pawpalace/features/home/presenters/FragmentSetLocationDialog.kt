package com.andriawan24.pawpalace.features.home.presenters

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.databinding.FragmentSetLocationDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FragmentSetLocationDialog : DialogFragment() {

    private val args: FragmentSetLocationDialogArgs by navArgs()
    private val binding: FragmentSetLocationDialogBinding by lazy {
        FragmentSetLocationDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme).apply {

            binding.editTextLocation.setText(args.location)

            binding.buttonSubmit.setOnClickListener {
                setFragmentResult(
                    SET_LOCATION_REQUEST_KEY,
                    bundleOf(SET_LOCATION_RESULT_KEY to binding.editTextLocation.text.toString())
                )
                findNavController().navigateUp()
            }

            setView(binding.root)
        }.create()
    }

    companion object {
        const val SET_LOCATION_REQUEST_KEY = "SET_LOCATION_REQUEST_KEY"
        const val SET_LOCATION_RESULT_KEY = "SET_LOCATION_RESULT_KEY"
    }
}