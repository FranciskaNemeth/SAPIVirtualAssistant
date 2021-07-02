package com.example.sapivirtualassistant.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.fragment.MainFragment
import com.example.sapivirtualassistant.fragment.OnPicHasChangedListener
import com.example.sapivirtualassistant.interfaces.GetUserInterface
import com.example.sapivirtualassistant.model.User
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity(), OnPicHasChangedListener{
    lateinit var navController : NavController
    lateinit var bottomNavView : BottomNavigationView
    lateinit var drawerLayout : DrawerLayout
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerNavView : NavigationView
    private lateinit var auth : FirebaseAuth
    lateinit var navUserProfile : CircleImageView

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


        val auth : FirebaseAuth = Firebase.auth
        if(auth.currentUser != null) {
            DatabaseManager.getUserData(auth.currentUser!!.email!!, object : GetUserInterface {
                override fun getUser(user: User) {
                    val headerView = drawerNavView.getHeaderView(0)
                    val navUsername = headerView.findViewById<View>(R.id.textViewName) as TextView
                    navUserProfile = headerView.findViewById<View>(R.id.imageViewProfile) as CircleImageView
                    navUsername.text = user.userName
                    val ref = Firebase.storage.reference.child("images/" + user.emailAddress + ".jpg")
                    var imgURL: String?
                    ref.downloadUrl.addOnSuccessListener { Uri ->
                        imgURL = Uri.toString()
                        Glide.with(this@MainActivity)
                            .load(imgURL)
                            .into(navUserProfile)
                    }
                }
            })
        }
        else
        {
            val headerView = drawerNavView.getHeaderView(0)
            val navUsername = headerView.findViewById<View>(R.id.textViewName) as TextView
            navUsername.text = "Vendég"
        }

        if (DatabaseManager.isGuest) {
            hideBottomNavItems()
            hideDrawerMenuItems()
        }
    }

    private fun showBottomNav() {
        bottomNavView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNavView.visibility = View.GONE
    }

    private fun hideBottomNavItems() {
        val navMenu: Menu = bottomNavView.getMenu()
        navMenu.findItem(R.id.profileFragment).isVisible = false
    }

    private fun hideDrawerMenu() {
        drawerNavView.visibility = View.GONE
    }

    private fun hideDrawerMenuItems() {
        val navMenu: Menu = drawerNavView.getMenu()
        navMenu.findItem(R.id.timetableFragment).isVisible = false
        navMenu.findItem(R.id.profileFragment).isVisible = false
        navMenu.findItem(R.id.logoutFragment).isVisible = false
    }

    override fun onSupportNavigateUp() : Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer_navigation_menu, menu)

        if (DatabaseManager.isGuest)
        {
            val navMenu: Menu = drawerNavView.getMenu()
            navMenu.findItem(R.id.logoutFragment).isVisible = false
        }
        else
        {
            val navMenu: Menu = drawerNavView.getMenu()
            navMenu.findItem(R.id.logoutFragment).isVisible = true
            val logOut : NavigationMenuItemView = findViewById(R.id.logoutFragment)
            logOut.setOnClickListener {
                auth = Firebase.auth

                val currentUser = auth.currentUser
                if(currentUser != null) {
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }
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

    override fun onPicHasChanged(imgURL : String) {
        Glide.with(this@MainActivity)
            .load(imgURL)
            .into(navUserProfile)
    }
}