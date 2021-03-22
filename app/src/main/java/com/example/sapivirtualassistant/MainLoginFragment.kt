package com.example.sapivirtualassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

class MainLoginFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_login_screen, container, false)

        val buttonOktato : Button = view.findViewById(R.id.buttonOktato)
        buttonOktato.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainLoginFragment_to_loginFragment)
        }

        val buttonHallgato : Button = view.findViewById(R.id.buttonHallgato)
        buttonHallgato.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainLoginFragment_to_loginFragment)
        }

        val buttonVendeg : Button = view.findViewById(R.id.buttonVendeg)
        buttonVendeg.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_mainLoginFragment_to_mainFragment)
        }

        return view
    }
}