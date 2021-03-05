package com.example.sapivirtualassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var navController : NavController
    lateinit var bottomNavView : BottomNavigationView
    lateinit var drawerLayout : DrawerLayout
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerNavView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment)
        bottomNavView = findViewById(R.id.bottomNavView)
        drawerLayout = findViewById(R.id.drawerLayout)
        drawerNavView = findViewById(R.id.drawerNavigationView)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        drawerNavView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
    }

    private fun showBottomNav() {
        bottomNavView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNavView.visibility = View.GONE
    }

    override fun onSupportNavigateUp() : Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer_navigation_menu, menu)
        val logOut : NavigationMenuItemView = findViewById(R.id.logoutFragment)
        logOut.setOnClickListener { finish() }
        return true
    }
}