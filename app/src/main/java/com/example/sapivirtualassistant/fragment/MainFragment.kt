package com.example.sapivirtualassistant.fragment


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean


class MainFragment : Fragment(), Callback<ResponseBody> {
    private var textView: TextView? = null
    private var imageView: ImageView? = null
    lateinit var auth : FirebaseAuth
    private lateinit var witInterface: WitInterface

    val locale = Locale("hun", "HU")

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(requireActivity(),
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = locale
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        witInterface = with(Retrofit.Builder()) {
            baseUrl("https://api.wit.ai/")
            with(build()) {
                create(WitInterface::class.java)
            }
        }

        auth = Firebase.auth

        textToSpeechEngine.speak("", TextToSpeech.QUEUE_FLUSH, null, "tts1")

        // This callback will only be called when this fragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    if(auth.currentUser != null)
                    {
                        //finish()
                        val a = Intent(Intent.ACTION_MAIN)
                        a.addCategory(Intent.CATEGORY_HOME)
                        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(a)
                    }
                    else {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    @SuppressLint("ClickableViewAccessibility") // i don't know what's this
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_screen, container, false)

        // checking permissions
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission()
        }

        textView = view.findViewById(R.id.textViewAnswer)
        imageView = view.findViewById(R.id.kabala)

        // initializing the SpeechRecognizer object and creating the intent for recognizing the speech
        val speechRecognizer : SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity())
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                textView?.setText("")
                textView?.setHint("Listening...")
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                imageView?.setImageResource(R.drawable.sapilogo)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                var specialStr = data!![0].toLowerCase(locale)

                specialStr = specialStr.replace("szabi", "Sapi")

                //specialStr = "Szia, Sapi"

                specialStr = specialStr.capitalize(locale)

                textView?.text = specialStr

                //textToSpeechEngine.speak(data[0], TextToSpeech.QUEUE_FLUSH, null, "tts1")

                // send Text Message
                /*sendTextMessageButton.setOnClickListener {
                    witInterface.forTextMessage(textMessageInput.text.toString()).enqueue(this)
                }*/
                witInterface.forTextMessage(specialStr).enqueue(this@MainFragment)

                /*val str = data[0].toLowerCase(locale)
                if (str == "helló" || str == "hello" || str == "hi" || str == "hallo" || str == "hali" || str == "szia")
                {
                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_profileFragment)
                }*/
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        imageView?.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                imageView?.setImageResource(R.drawable.sapilogo_hatter)
                speechRecognizer.startListening(speechRecognizerIntent)
            }
            false
        }

        return view
    }

    // a bug occurs here -> TODO: fix this bug
    /*override fun onDestroy() {
        super.onDestroy()
        speechRecognizer!!.destroy()
    }*/

    // if the permission is not granted
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                requireActivity(),
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val RecordAudioRequestCode = 1

        /**
         * Client Access Token of Wit.ai App
         */
        private const val CLIENT_ACCESS_TOKEN = "PZT3GXMUFCGPRW4Y67SEJMFS4WK7LDRV"
    }

    interface WitInterface {
        @Headers("Authorization: Bearer $CLIENT_ACCESS_TOKEN")
        @GET("/message")
        fun forTextMessage(
            @Query(value = "q") message: String,
            @Query(value = "v") version: String = "20200513"
        ): Call<ResponseBody>
    }

    // Handles response from Wit.ai App
    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        if (response.body() == null) return
        // get the JSON Object sent by Wit.ai
        val data = JSONObject(response.body()!!.string())
        try {
            val res = data
            Log.d("WIT", "data: $res")

            // get most confident Intent
            //val intent = data.getJSONArray("intents").mostConfident() ?: return
            //Log.d("WIT", "intent: $intent")

            // get most confident wit$number:first Entity
            /*val entity = data.getJSONObject("entities")
                .getJSONArray("wit\$entities:first").mostConfident()?.get("value")?.toString()
                ?.toDoubleOrNull() ?: return*/
            //val entity = data.getJSONObject("entities")
            //    .getJSONArray("entities").mostConfident()?.get("name")?.toString()
            //    ?.toDoubleOrNull() ?: return
            //Log.d("WIT", "entity: $entity")



            // based on Intent set text in result TextView
            /*result.text = when (intent.getString("name")) {
                "add_num" -> number1 + number2
                "sub_num" -> number1 - number2
                "mul_num" -> number1 * number2
                "div_num" -> number1 / number2
                else -> ""
            }.toString()*/
        } catch (e: Exception) {
            Log.e("OnResponse", "Error getting Entities or Intent", e)
        }
    }

    /**
     * JSONArray Extension function to get most confident object in it
     *
     * @return Most Confident JSONObject
     */
    private fun JSONArray.mostConfident(): JSONObject? {
        var confidentObject: JSONObject? = null
        var maxConfidence = 0.0
        for (i in 0 until length()) {
            try {
                val obj = getJSONObject(i)
                val currConfidence = obj.getDouble("confidence")
                if (currConfidence > maxConfidence) {
                    maxConfidence = currConfidence
                    confidentObject = obj
                }
            } catch (e: JSONException) {
                Log.e("WIT", "mostConfident: ", e)
            }
        }
        return confidentObject
    }

    // On failure just log it
    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Log.e("WIT", "API call failed")
    }
}