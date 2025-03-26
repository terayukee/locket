package com.ssafy.locket.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.ssafy.locket.R
import com.ssafy.locket.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigationBar()
    }

    fun initNavigationBar(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        bottomNavigationView.setOnItemSelectedListener { item ->
            navController.currentDestination?.id?.let {
                val navigateOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(it, true)
                    .build()

                when (item.itemId) {
                    R.id.home -> navController.navigate(R.id.homeFragment, null, navigateOptions)
                    R.id.household_account_book -> navController.navigate(R.id.financeFragment, null, navigateOptions)
                    R.id.lowest_price_graph -> navController.navigate(R.id.productListFragment, null, navigateOptions)
                    R.id.payment -> navController.navigate(R.id.cardPaymentFragment, null, navigateOptions)
                    else -> false
                }
            }



            true
        }
        hideBottomNavigationView(navController)
    }

    private fun hideBottomNavigationView(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.visibility = when (destination.id) {
                R.id.homeFragment -> View.VISIBLE
                R.id.financeFragment -> View.VISIBLE
                R.id.productListFragment -> View.VISIBLE
                R.id.cardPaymentFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    fun changeBackgroundColor(colorRes: Int) {
        findViewById<View>(R.id.main)?.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    fun setBottomNavigationIndex(id: Int) {
        binding.bottomNavigation.selectedItemId = id
    }
}