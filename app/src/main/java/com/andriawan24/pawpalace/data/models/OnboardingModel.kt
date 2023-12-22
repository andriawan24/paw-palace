package com.andriawan24.pawpalace.data.models

import androidx.annotation.DrawableRes

data class OnboardingModel(
    @DrawableRes val image: Int,
    val title: String,
    val subtitle: String
)
