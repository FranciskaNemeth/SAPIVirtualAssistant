package com.example.sapivirtualassistant.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.activity.MainActivity
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.utils.UtilsClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainLoginFragment : Fragment() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            DatabaseManager.getUserData(currentUser.email!!)
            startActivity(Intent(context, MainActivity::class.java))
        }

        // This callback will only be called when this fragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event

                    //finish()
                    val a = Intent(Intent.ACTION_MAIN)
                    a.addCategory(Intent.CATEGORY_HOME)
                    a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(a)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_login_screen, container, false)

        val buttonBejelentkezes : Button = view.findViewById(R.id.buttonBejelentkezes)
        buttonBejelentkezes.setOnClickListener {
            DatabaseManager.isGuest = false
            setFragmentResult("requestKey", bundleOf("name" to "Bejelentkezés"))
            Navigation.findNavController(view).navigate(R.id.action_mainLoginFragment_to_loginFragment)
        }

        val buttonVendeg : Button = view.findViewById(R.id.buttonVendeg)
        buttonVendeg.setOnClickListener{
            val intent = Intent(context, MainActivity::class.java)
            DatabaseManager.isGuest = true
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        if( !UtilsClass().isNetworkAvailable(requireContext()) ) {
            AlertDialogFragment().errorHandling(requireContext())
        }

        /*if(!UtilsClass().isInternetAvailable()) {
            AlertDialogFragment().errorHandling(requireContext())
        }*/

        super.onResume()
    }
}