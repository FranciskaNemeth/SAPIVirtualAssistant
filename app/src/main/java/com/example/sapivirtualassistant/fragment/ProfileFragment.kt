package com.example.sapivirtualassistant.fragment

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    lateinit var datePickerButton : ImageView
    lateinit var datePickerText : EditText
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = User(0, null,"Alma", "alma@gmail.com", "0774995367", "1998.apr.30")

        val hashUser = user.userToHashMapOf()

        /*db.collection("users")
            .add(hashUser)
            .addOnSuccessListener { documentReference ->
                Log.d("USER", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("USER", "Error adding document", e)
            }*/
        //vagy
        db.collection("users").document(user.emailAddress)
            .set(hashUser)
            .addOnSuccessListener { documentReference ->
                Log.d("USER", "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w("USER", "Error adding document", e)
            }
        //Log.d("USER", user.toString())
        //user.userName = "Korte"
        //Log.d("USER", user.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_screen, container, false)

        datePickerButton = view.findViewById(R.id.imageViewCalendar)
        datePickerText = view.findViewById(R.id.editTextBirthDate)

        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        datePickerText.setOnClickListener {
            showDatePickerDialog()
        }

        return view
    }

    fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        val currentDay = Date()
        val c = Calendar.getInstance()

        c.time = currentDay
        c.add(Calendar.YEAR, -17)

        val maxYear = c.get(1)

        c.set(maxYear, 11, 31)
        
        val maxDate = c.time.time

        datePickerDialog.datePicker.maxDate = maxDate
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        /*val date = "$year.$month.$dayOfMonth"
        datePickerText.setText(date)*/

        val sdf = SimpleDateFormat("yyyy.MMMM.dd")
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateString: String = sdf.format(calendar.time)
        datePickerText.setText(dateString)
    }
}