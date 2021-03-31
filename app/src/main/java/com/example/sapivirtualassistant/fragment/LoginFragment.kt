package com.example.sapivirtualassistant.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.activity.MainActivity

class LoginFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_screen, container, false)

        val buttonLogin : Button = view.findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
        }

        val buttonHelp : Button = view.findViewById(R.id.buttonHelp)
        buttonHelp.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_helpFragment)
        }

        val buttonFeedback : Button = view.findViewById(R.id.buttonFeedback)
        buttonFeedback.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_feedbackFragment)
        }

        return view
    }
}