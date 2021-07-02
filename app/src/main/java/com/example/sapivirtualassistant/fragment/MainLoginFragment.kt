package com.example.sapivirtualassistant.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.activity.MainActivity
import com.example.sapivirtualassistant.database.DatabaseManager
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_login_screen, container, false)

        val buttonBejelentkezes : Button = view.findViewById(R.id.buttonBejelentkezes)
        buttonBejelentkezes.setOnClickListener {
            setFragmentResult("requestKey", bundleOf("name" to "Bejelentkezés"))
            Navigation.findNavController(view).navigate(R.id.action_mainLoginFragment_to_loginFragment)
        }

        val buttonVendeg : Button = view.findViewById(R.id.buttonVendeg)
        buttonVendeg.setOnClickListener{
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("Guest", true)
            startActivity(intent)
        }

        return view
    }
}