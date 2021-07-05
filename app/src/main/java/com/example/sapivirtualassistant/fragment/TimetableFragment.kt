package com.example.sapivirtualassistant.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.interfaces.GetUserInterface
import com.example.sapivirtualassistant.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class TimetableFragment : Fragment() {
    lateinit var timeTable : ImageView
    val auth : FirebaseAuth = Firebase.auth
    lateinit var ref : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.timetable_screen, container, false)

        timeTable = view.findViewById(R.id.timeTableImage)

        if (auth.currentUser != null) {
            DatabaseManager.getUserData(auth.currentUser!!.email!!, object : GetUserInterface {
                override fun getUser(user: User) {

                    when (user.userType) {
                        0 -> {
                            ref = Firebase.storage.reference.child("timetables/" + user.teacherTimeTable + ".jpg")
                            setTimeTable(ref)
                        }
                        1 -> {
                            ref = Firebase.storage.reference.child("timetables/" + user.className + "." + user.classGrade + "." + user.classGroup + ".jpg")
                            setTimeTable(ref)
                        }
                    }
                }
            })
        }

        return view
    }

    private fun setTimeTable(ref : StorageReference) {
        ref.downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(requireActivity())
                .load(Uri.toString())
                .into(timeTable)
        }
    }
}