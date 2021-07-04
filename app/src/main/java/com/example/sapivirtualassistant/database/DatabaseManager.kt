package com.example.sapivirtualassistant.database

import android.util.Log
import com.example.sapivirtualassistant.interfaces.GetAppsInterface
import com.example.sapivirtualassistant.interfaces.GetHelpModelInterface
import com.example.sapivirtualassistant.interfaces.GetResponsesInterface
import com.example.sapivirtualassistant.interfaces.GetUserInterface
import com.example.sapivirtualassistant.model.FeedbackModel
import com.example.sapivirtualassistant.model.HelpModel
import com.example.sapivirtualassistant.model.User
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object DatabaseManager {
    lateinit var user : User
    lateinit var helpModel: HelpModel
    lateinit var helpList : MutableList<HelpModel>
    lateinit var responseMap : Map<String,Map<String, List<String>>>
    lateinit var appsList : MutableMap<String, String>
    var isGuest : Boolean = false

    fun getApps(getAppsInterface: GetAppsInterface? = null) {
        Firebase.firestore.collection("apps").document("apps").get()
            .addOnSuccessListener { document ->
                appsList = document.data as MutableMap<String, String>
                getAppsInterface?.getApps(appsList)
            }
            .addOnFailureListener { exception ->
                Log.d("RES", "Error getting apps:  ", exception)
            }
    }

    fun getWitResponses(getResponsesInterface: GetResponsesInterface? = null) {
        Firebase.firestore.collection("response").get()
            .addOnSuccessListener { qs : QuerySnapshot ->
                responseMap = getResponseMap(qs)
                getResponsesInterface?.getResponses(responseMap)
            }
            .addOnFailureListener { exception ->
                Log.d("RES", "Error getting response documents: ", exception)
            }
    }

    fun getResponseMap(querySnapshot: QuerySnapshot) : Map<String,Map<String, List<String>>> {
        var resMap : MutableMap<String, Map<String, List<String>>> = HashMap()
        for (doc in querySnapshot.documents) {
            resMap[doc.id] = doc.data as Map<String, List<String>>
        }

        return resMap
    }

    fun getUserData(email: String, getUserInterface: GetUserInterface? = null) {
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

    fun updateUserData(u: User) {
        Firebase.firestore.collection("users").document(u.emailAddress)
            .set(u.userToHashMapOf())
            .addOnSuccessListener { documentReference ->
                user = u
            }
            .addOnFailureListener { e ->
                Log.w("USER", "Error adding document", e)
            }
    }

    private fun mapToUser(map: MutableMap<String, Any>) : User {
        return User(
            userType = Integer.parseInt(map["userType"].toString()),
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

    private fun mapToHelpModel(map: MutableMap<String, Any>) : HelpModel {
        return HelpModel(
            question = map["q"].toString(),
            answer = map["a"].toString(),
            type = map["type"].toString()
        )
    }

    fun insertFeedbackData(feedbackModel: FeedbackModel) {

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val calendar = Calendar.getInstance()
        //calendar.set(year, month, dayOfMonth)
        val dateString: String = sdf.format(calendar.time)

        val feedbackData = hashMapOf(
            "rating" to feedbackModel.rating,
            "message" to feedbackModel.message,
            "email" to feedbackModel.email
        )

        Firebase.firestore.collection("feedback")
            .document(dateString)
            .set(feedbackData)
            .addOnSuccessListener { document ->
                Log.d("MUKI", "Success ")
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    public fun responseMapIsInitialized() : Boolean {
        return this::responseMap.isInitialized
    }
}