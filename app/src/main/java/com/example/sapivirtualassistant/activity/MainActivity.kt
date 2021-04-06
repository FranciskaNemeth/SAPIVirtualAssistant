package com.example.sapivirtualassistant.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.database.DatabaseManager
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    lateinit var navController : NavController
    lateinit var bottomNavView : BottomNavigationView
    lateinit var drawerLayout : DrawerLayout
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerNavView : NavigationView
    private lateinit var auth : FirebaseAuth

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
                R.id.mainFragment -> {
                    showBottomNav()
                }
                else -> hideBottomNav()
            }
        }

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        drawerNavView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)

        /*val headerLayout: View = drawerNavView.inflateHeaderView(R.layout.header)
        val uName = headerLayout.findViewById<TextView>(R.id.textViewName)
        uName.text = DatabaseManager.user.userName*/
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
        logOut.setOnClickListener {
            auth = Firebase.auth

            val currentUser = auth.currentUser
            if(currentUser != null) {
                FirebaseAuth.getInstance().signOut()
                finish()
            }
        }

        val calendar : NavigationMenuItemView = findViewById(R.id.calendarFragment)
        calendar.setOnClickListener {
            val calendarUri: Uri = CalendarContract.CONTENT_URI
                .buildUpon()
                .appendPath("time")
                .build()
            startActivity(Intent(Intent.ACTION_VIEW, calendarUri))
        }

        val cal : BottomNavigationItemView = findViewById(R.id.calFragment)
        cal.setOnClickListener {
            val calendarUri: Uri = CalendarContract.CONTENT_URI
                .buildUpon()
                .appendPath("time")
                .build()
            startActivity(Intent(Intent.ACTION_VIEW, calendarUri))
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return false
    }
}