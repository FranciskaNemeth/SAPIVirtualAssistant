package com.example.sapivirtualassistant.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.model.FeedbackModel
import com.google.android.material.textfield.TextInputEditText

class FeedbackFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.feedback_screen, container, false)

        val ratingBar : RatingBar = view.findViewById(R.id.ratingBar)
        val textInputEditTextEmail : TextInputEditText = view.findViewById(R.id.textInputEditTextEmail)
        val textInputEditTextMessage : TextInputEditText = view.findViewById(R.id.textInputEditTextMessage)
        val buttonSend : Button = view.findViewById(R.id.buttonSend)

        buttonSend.setOnClickListener {
            if (isValidEmail(textInputEditTextEmail.text.toString())) {
                val feedbackModel = FeedbackModel(textInputEditTextEmail.text.toString(), textInputEditTextMessage.text.toString(), ratingBar.rating)
                DatabaseManager.insertFeedbackData(feedbackModel)
                requireActivity().onBackPressed()
            }
            else {
                Toast.makeText(context, "Helytelen e-mail cím!", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}