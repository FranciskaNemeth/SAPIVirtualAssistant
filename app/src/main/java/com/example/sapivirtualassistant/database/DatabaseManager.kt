package com.example.sapivirtualassistant.database

import android.util.Log
import com.example.sapivirtualassistant.interfaces.GetHelpModelInterface
import com.example.sapivirtualassistant.interfaces.GetUserInterface
import com.example.sapivirtualassistant.model.HelpModel
import com.example.sapivirtualassistant.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DatabaseManager {
    lateinit var user : User
    lateinit var helpModel: HelpModel
    lateinit var helpList : MutableList<HelpModel>

    fun getUserData(email : String, getUserInterface : GetUserInterface? = null) {
        Firebase.firestore.collection("users").document(email)
            .get()
            .addOnSuccessListener { document ->
                user = mapToUser(document.data!!)
                getUserInterface?.getUser(user)
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
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
                    birthDay = map["birthDay"] as String?,
                    className = map["className"] as String?,
                    classGrade = map["classGrade"] as String?,
                    classGroup = map["classGroup"] as String?,
                    teacherTimeTable = map["teacherTimeTable"] as String?
        )
    }

    fun getHelpData(getHelpModelInterface: GetHelpModelInterface? = null) {
        Firebase.firestore.collection("help")
            .get()
            .addOnSuccessListener { document ->
                helpList = ArrayList()
                for (doc in document.documents) {
                    helpModel = mapToHelpModel(doc.data!!)
                    helpList.add(helpModel)
                }

                getHelpModelInterface?.getHelpModel(helpList)
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun mapToHelpModel(map : MutableMap<String, Any>) : HelpModel {
        return HelpModel(question = map["q"].toString(), answer = map["a"].toString(), type = map["type"].toString())
    }
}