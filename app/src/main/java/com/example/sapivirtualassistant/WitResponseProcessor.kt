package com.example.sapivirtualassistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.Navigation
import org.json.JSONArray
import org.json.JSONObject

class WitResponseProcessor(private val responseMap: Map<String, Map<String, List<String>>>) {

    private val traitMap = getTraitMap(responseMap)
    private lateinit var appMap: MutableMap<String, String>
    private val specialApps = listOf("calendar", "phone", "alarm")
    private val  navigateList = listOf("profile", "timetable", "help", "feedback")

    fun processWitResponse(data : JSONObject, context: Context, view: View) : String? {

        val intent = extractIntent(data)
        val trait = extractTrait(data)

        Log.d("WIT", "Intent: $intent trait: $trait")
        var result: Boolean = false
        if (intent != null) {
            if (isOpenIntent(intent)) {
                if (intent == "weather") {
                    result = openApp("weather", context)
                }
                else {
                    if (trait != null) {
                        if (canOpenApp(trait)) {
                            result = openApp(trait, context)
                        } else if (canNavigateScreens(trait)) {
                            result = navigateScreens(trait, context, view)
                        }
                    }
                }
            }
            else {
                result = false
            }
        }

        if (!result) {
            return getResponse(intent, trait)
        }

        return null

    }

    fun setAppList(appList: MutableMap<String, String>) {
        this.appMap = appList
    }

    private fun extractIntent(data: JSONObject): String? {
        val listOfIntents = data["intents"] as JSONArray

        if (listOfIntents.length() == 0) {
            return null
        }

        val sortedList = mutableListOf<JSONObject>()

        for (index in 0 until listOfIntents.length()) {
            val intent: JSONObject = listOfIntents.getJSONObject(index)
            sortedList.add(intent)
        }

        sortedList.sortBy { it.getDouble("confidence") }
        sortedList.reverse()

        return sortedList[0].getString("name")
    }

    private fun extractTrait(data : JSONObject) : String? {
        val traits = data["traits"] as JSONObject

        var highestConfidenceKey: String? = null
        var highestConfidence = 0.0

        val keys = traits.keys()
        keys.forEach {
            val item = traits[it] as JSONArray
            val firstItem = item[0] as JSONObject
            val confidence = firstItem.getDouble("confidence")
            if (confidence > highestConfidence) {
                highestConfidenceKey = it
                highestConfidence = confidence
            }
        }

        return highestConfidenceKey
    }

    private fun getResponse(intent: String?, trait: String?): String {
        when {
            intent != null -> {
                if (intent == "open") {
                    return getDefaultResponse()
                }

                if(!this.responseMap.containsKey(intent)) {
                    return getDefaultResponse()
                }

                val traitMapForIntent = this.responseMap[intent]

                var responseList = traitMapForIntent!!["default"]
                if (trait != null) {
                    responseList = traitMapForIntent[trait]
                    Log.d("WIT", "1")
                }
                Log.d("WIT", "2")

                val randomIndex = responseList?.let { rand(it.size) }
                return responseList!![randomIndex!!]
            }
            trait != null -> {

                val responseList = this.traitMap[trait]
                if(responseList == null) {
                    return getDefaultResponse()
                }
                val randomIndex = responseList?.let { rand(it.size) }
                Log.d("WIT", "3")

                return responseList!![randomIndex!!]

            }
            else -> {
                Log.d("WIT", "4")
                return getDefaultResponse()
            }
        }
    }

    private fun getDefaultResponse(): String {
        val defaultIntent = this.responseMap["default"]

        val defaultResponseList = defaultIntent!!["default"]

        val randomIndex = defaultResponseList?.let { rand(it.size) }
        return defaultResponseList!![randomIndex!!]
    }

    private fun getTraitMap(responseMap : Map<String,Map<String, List<String>>>): Map<String, List<String>> {
        val copiedMap = responseMap.toMap()
        val keys = copiedMap.keys
        val traitMap = mutableMapOf<String, List<String>>()
        keys.forEach { intentKey ->
            val intentMap = copiedMap[intentKey]

            val traitKeys = intentMap!!.keys

            traitKeys.forEach { traitKey ->
                if (traitMap.containsKey(traitKey)) {
                    val responseList: ArrayList<String> = traitMap[traitKey] as ArrayList<String>

                    intentMap[traitKey]?.let { responseList.addAll(it.toMutableList()) }
                }
                else {
                    intentMap[traitKey]?.let { traitMap.put(traitKey, it.toMutableList()) }
                }
            }
        }
        //Log.d("WIT", "Trait map: $traitMap")
        //Log.d("WIT", "ResponseMap")
        return traitMap
    }

    private fun isOpenIntent(intent: String) : Boolean {
        return intent == "open" || intent == "weather"
    }

    private fun openApp(trait: String?, context: Context): Boolean {
        val intent: Intent?
        when {
            trait == "calendar" -> {
                val calendarUri: Uri = CalendarContract.CONTENT_URI
                    .buildUpon()
                    .appendPath("time")
                    .build()
                intent = Intent(Intent.ACTION_VIEW, calendarUri)
                startActivity(context, Intent(Intent.ACTION_VIEW, calendarUri), Bundle())
            }
            trait == "alarm" -> {
                intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            trait == "phone" -> {
                intent = Intent(Intent.ACTION_DIAL)
            }
            this.appMap.containsKey(trait) -> {
                val appID = this.appMap[trait].toString()
                val pm = context.packageManager

                // Initialize a new Intent
                intent = pm.getLaunchIntentForPackage(appID)

                // Add category to intent
                intent?.addCategory(Intent.CATEGORY_LAUNCHER)
            }
            else -> {
                intent = null
            }
        }

        if (intent == null) {
            return false
        } else {
            startActivity(context, intent, Bundle())
        }
        return true
    }

    private fun canOpenApp(trait: String): Boolean {
        return this.specialApps.contains(trait) || this.appMap.containsKey(trait)
    }

    private fun canNavigateScreens(trait: String): Boolean {
        return this.navigateList.contains(trait)
    }

    private fun navigateScreens(trait: String?, context: Context, view: View) : Boolean {
        when (trait) {
            "profile" -> {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_profileFragment)
            }
            "timetable" -> {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_timetableFragment)
            }
            "help" -> {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_helpFragment)
            }
            "feedback" -> {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_feedbackFragment)
            }
            else -> {
                return false
            }
        }
        return true
    }

    fun correctTextForSpeech(oldString : String) : String {
        var newString = oldString.replace("Sapienti", "szapienci")
        newString = newString.replace("Sapi", "szapi")
        newString = newString.replace("SAPI", "szapi")
        return newString

    }

    private fun rand(end: Int): Int {
        require(end > 0)  { "Illegal Argument" }
        return (0 until end).random()
    }

    fun responseManagerIsInitialized() : Boolean {
        return this::appMap.isInitialized
    }
}
