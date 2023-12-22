package com.andriawan24.pawpalace.features.onboarding.viewmodels

import androidx.lifecycle.ViewModel
import com.andriawan24.pawpalace.data.repositories.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingVM @Inject constructor(
    private val onboardingRepository: OnboardingRepository
): ViewModel() {


}