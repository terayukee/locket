package com.ssafy.locket.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssafy.locket.R
import com.ssafy.locket.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


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
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.homeFragment) // homeFragment로 이동
                R.id.household_account_book -> navController.navigate(R.id.financeFragment) // financeFragment로 이동
                R.id.lowest_price_graph -> navController.navigate(R.id.productListFragment) // productListFragment로 이동
                R.id.payment -> navController.navigate(R.id.cardPaymentFragment) // cardPaymentFragment로 이동
                else -> false
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
}