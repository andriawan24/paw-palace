package com.andriawan24.pawpalace.features

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        val navHostController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostController.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.action_global_home)
                }
                R.id.chatFragment -> {
                    navController.navigate(R.id.action_global_chat)
                }
                R.id.historyFragment -> {
                    navController.navigate(R.id.action_global_history)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.action_global_profile)
                }
            }

            true
        }

        navController.addOnDestinationChangedListener { _, destination,_ ->
            binding.bottomNavigationView.isVisible = destination.id in listOf(
                R.id.homeFragment,
                R.id.chatFragment,
                R.id.historyFragment,
                R.id.profileFragment
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}