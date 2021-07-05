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
import android.text.method.ScrollingMovementMethod
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
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.WitResponseProcessor
import com.example.sapivirtualassistant.activity.LoginActivity
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.interfaces.GetAppsInterface
import com.example.sapivirtualassistant.interfaces.GetResponsesInterface
import com.example.sapivirtualassistant.utils.UtilsClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.*


class MainFragment : Fragment(), Callback<ResponseBody> {
    private var textView: TextView? = null
    private var imageView: ImageView? = null
    lateinit var auth : FirebaseAuth
    private lateinit var witInterface: WitInterface
    private lateinit var responseProcessor: WitResponseProcessor
    lateinit var appMutableMap: MutableMap<String, String>

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
    }

    @SuppressLint("ClickableViewAccessibility")
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

        textView?.movementMethod = ScrollingMovementMethod()

        DatabaseManager.getWitResponses(object : GetResponsesInterface {
            override fun getResponses(responseMap: Map<String, Map<String, List<String>>>) {
                responseProcessor = WitResponseProcessor(responseMap)
            }

        })

        DatabaseManager.getApps(object : GetAppsInterface {
            override fun getApps(appsList: MutableMap<String, String>) {
                appMutableMap = appsList
            }
        })

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
                textView?.setHint("Hallgatlak...")
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {
                textView?.setText("")
                textView?.setHint("")
                imageView?.setImageResource(R.drawable.sapilogo)
            }
            override fun onError(i: Int) {
                imageView?.setImageResource(R.drawable.sapilogo)
                Toast.makeText(requireActivity(), "Hiba történt a hangfelismerésnél! Próbáld újra vagy próbálkozz később.", Toast.LENGTH_LONG).show()
            }
            override fun onResults(bundle: Bundle) {
                imageView?.setImageResource(R.drawable.sapilogo)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                var specialStr = data!![0].toLowerCase(locale)

                specialStr = specialStr.replace("szabi", "Sapi")

                specialStr = specialStr.capitalize(locale)

                textView?.text = specialStr

                witInterface.forTextMessage(specialStr).enqueue(this@MainFragment)
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        imageView?.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (!responseProcessor.responseManagerIsInitialized()) {
                    responseProcessor.setAppList(appMutableMap)
                }

                if (DatabaseManager.responseMapIsInitialized() && DatabaseManager.responseMap.isNotEmpty()) {
                    Log.d("RES", "${DatabaseManager.responseMap}")
                    imageView?.setImageResource(R.drawable.sapilogo_hatter)
                    speechRecognizer.startListening(speechRecognizerIntent)
                }
                else {
                    val alertDialogFragment : AlertDialogFragment = AlertDialogFragment()
                    alertDialogFragment.errorHandling(requireContext())
                }

            }
            false
        }

        return view
    }

    override fun onResume() {
        if( !UtilsClass().isNetworkAvailable(requireContext()) ) {
            AlertDialogFragment().errorHandling(requireContext())
        }

        super.onResume()
    }

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
        Log.d("WIT", "data: $data")

        val res = view?.let { responseProcessor.processWitResponse(data, requireContext(), it) }
        Log.d("WIT", "sorted: $res")

        if (res != null) {
            textView?.text = res
            textToSpeechEngine.speak(responseProcessor.correctTextForSpeech(res), TextToSpeech.QUEUE_FLUSH, null, "tts1")
        }
    }
    
    // On failure just log it
    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Log.e("WIT", "API call failed")
    }
}