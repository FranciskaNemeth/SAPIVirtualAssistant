package com.example.sapivirtualassistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.activity.MainActivity
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.utils.UtilsClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_screen, container, false)

        val emailEditText : TextInputEditText = view.findViewById(R.id.textInputEditTextUserName)
        val pwdEditText : TextInputEditText = view.findViewById(R.id.textInputEditTextPassword)
        val oktatoVagyHallgatoTextView : TextView = view.findViewById(R.id.textView)

        setFragmentResultListener("requestKey") { key, bundle ->
            val result = bundle.getString("name")
            oktatoVagyHallgatoTextView.text = result
        }

        val buttonLogin : Button = view.findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            if (emailEditText.text.isNullOrBlank() || emailEditText.text.isNullOrEmpty() || pwdEditText.text.isNullOrBlank() || pwdEditText.text.isNullOrEmpty())
            {
                Toast.makeText(requireActivity(), "Üres az e-mail cím vagy a jelszó mező!",
                    Toast.LENGTH_LONG).show()
            }
            else {
                if (isValidEmail(emailEditText.text.toString())) {
                    login(emailEditText.text.toString(), pwdEditText.text.toString())
                }
                else {
                    Toast.makeText(requireActivity(), "Helytelen e-mail cím vagy jelszó!",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        val buttonHelp : Button = view.findViewById(R.id.buttonHelp)
        buttonHelp.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_helpFragment)
        }

        val buttonFeedback : Button = view.findViewById(R.id.buttonFeedback)
        buttonFeedback.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_feedbackFragment)
        }

        val buttonForgotPassword : Button = view.findViewById(R.id.buttonForgotPwd)
        buttonForgotPassword.setOnClickListener {
            if (emailEditText.text.isNullOrBlank() || emailEditText.text.isNullOrEmpty())
            {
                Toast.makeText(requireActivity(), "Üres az e-mail cím mező!",
                    Toast.LENGTH_LONG).show()
            }
            else {
                if (isValidEmail(emailEditText.text.toString())) {
                    forgotPassword(emailEditText.text.toString())
                }
                else {
                    Toast.makeText(requireActivity(), "Nem létező vagy helytelen e-mail cím!",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    override fun onResume() {
        if( !UtilsClass().isNetworkAvailable(requireContext()) ) {
            AlertDialogFragment().errorHandling(requireContext())
        }

        super.onResume()
    }

    fun login(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    DatabaseManager.getUserData(email)
                    startActivity(Intent(context, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireActivity(), "Helytelen e-mail cím vagy jelszó!",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    fun forgotPassword(email : String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireActivity(), "Ellenőrizze az e-mailjeit!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireActivity(), "Próbálja meg újra!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}