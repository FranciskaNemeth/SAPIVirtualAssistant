package com.example.sapivirtualassistant.database

import android.util.Log
import com.example.sapivirtualassistant.interfaces.GetUserInterface
import com.example.sapivirtualassistant.model.User
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DatabaseManager {
    lateinit var user : User

    fun getUserData(email : String) {
        Firebase.firestore.collection("users").document(email)
            .get()
            .addOnSuccessListener { document ->
                user = mapToUser(document.data!!)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    fun updateUserData(u : User) {
        Firebase.firestore.collection("users").document(u.emailAddress)
            .set(u.userToHashMapOf())
            .addOnSuccessListener { documentReference ->
                user = u
            }
            .addOnFailureListener { e ->
                Log.w("USER", "Error adding document", e)
            }
    }

    private fun mapToUser(map : MutableMap<String, Any>) : User {
        return User(userType = Integer.parseInt(map["userType"].toString()),
                    profilePicture = map["userProfile"] as String?,
                    userName = map["userName"] as String,
                    emailAddress = map["emailAddress"] as String,
                    phoneNumber = map["phoneNumber"] as String?,
                    birthDay = map["birthDay"] as String?
        )
    }

}