package com.example.sapivirtualassistant

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import java.util.*


class MainFragment : Fragment() {
    private var textView: TextView? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
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
                val specialStr = data!![0].toLowerCase()
                if(specialStr == "szia szabi")
                {
                    textView?.text = "Szia Sapi"
                }
                else
                {
                    textView?.text = data[0]
                }
                val str = data[0].toLowerCase()
                if (str == "helló" || str == "hello" || str == "hi" || str == "hallo" || str == "hali" || str == "szia")
                {
                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_profileFragment)
                }
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
    }
}