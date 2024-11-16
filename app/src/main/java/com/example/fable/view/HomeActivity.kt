package com.example.fable.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fable.R
import com.example.fable.databinding.ActivityHomeBinding
import com.example.fable.util.Util
import com.example.fable.view.component.snackbar.MySnackBar
import com.example.fable.view.create.CreateActivity
import com.example.fable.view.explore.ExploreActivity
import com.example.fable.view.home.HomeFragment
import com.example.fable.view.home.HomeViewModel
import com.example.fable.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by lazy {
        val factory = ViewModelFactory.getInstance(this)
        factory.create(HomeViewModel::class.java)
    }

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
                R.id.navigation_home -> getString(R.string.title_home)
                R.id.navigation_profile -> getString(R.string.title_profile)
                else -> getString(R.string.app_name)
            }
            binding.topAppBar.title = pageTitle
        }

        binding.topAppBar.setNavigationIcon(R.drawable.ic_travel_explore_24)
        binding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        binding.topAppBar.inflateMenu(
            R.menu.top_appbar_menu
        )

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    viewModel.viewModelScope.launch {
                        viewModel.logout()
                    }
                    MySnackBar.showSnackBar(binding.root, getString(R.string.logout_successfully))
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, WelcomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }, Util.ONE_SECOND)
                }
                else -> false
            }
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