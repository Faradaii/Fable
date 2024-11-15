package com.example.fable.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fable.R
import com.example.fable.databinding.ActivityHomeBinding
import com.example.fable.view.create.CreateActivity
import com.example.fable.view.explore.ExploreActivity
import com.example.fable.view.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.background = null
        navView.menu.getItem(1).isEnabled = false

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val pageTitle = when (destination.id) {
                R.id.navigation_home -> "Home"
                R.id.navigation_profile -> "Profile"
                else -> "Fable"
            }
            binding.topAppBar.title = pageTitle
        }

        binding.topAppBar.setNavigationIcon(R.drawable.ic_travel_explore_24)
        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        val createActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navView.selectedItemId = R.id.navigation_home
                navHostFragment.childFragmentManager.fragments.firstOrNull {
                    it is HomeFragment
                }?.let { fragment -> (fragment as HomeFragment).getStories() }
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            createActivityLauncher.launch(intent)
        }

    }
}