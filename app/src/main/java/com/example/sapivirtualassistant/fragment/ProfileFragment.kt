package com.example.sapivirtualassistant.fragment

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.model.User
import com.example.sapivirtualassistant.utils.UtilsClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    lateinit var datePickerButton : ImageView
    lateinit var datePickerText : EditText
    lateinit var userName : TextInputEditText
    lateinit var email : EditText
    lateinit var phoneNumber : EditText
    lateinit var profilePicture : CircleImageView
    lateinit var pencilButton : ImageView
    lateinit var user : User
    lateinit var buttonSave : Button
    private var imageUri: Uri? = null
    val storage = Firebase.storage
    var imgURL : String? = null
    lateinit var imgURLToSend : OnPicHasChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseManager.getUserData(Firebase.auth.currentUser!!.email!!)
        user = DatabaseManager.user

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_screen, container, false)

        datePickerButton = view.findViewById(R.id.imageViewCalendar)
        datePickerText = view.findViewById(R.id.editTextBirthDate)
        userName = view.findViewById(R.id.textInputEditTextUserName)
        email = view.findViewById(R.id.editTextEmail)
        phoneNumber = view.findViewById(R.id.editTextPhoneNumber)
        buttonSave = view.findViewById(R.id.buttonSave)
        profilePicture = view.findViewById(R.id.profileImage)
        pencilButton = view.findViewById(R.id.imageViewPencil)

        datePickerText.setText(user.birthDay)
        userName.setText(user.userName)
        email.setText(user.emailAddress)
        phoneNumber.setText(user.phoneNumber)

        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        datePickerText.setOnClickListener {
            showDatePickerDialog()
        }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                imageUri = intent?.data
                imgURL = imageUri.toString()
                imgURLToSend.onPicHasChanged(imgURL!!)
                profilePicture.setImageURI(imageUri)
                val storageRef = storage.reference
                val mountainsRef = storageRef.child("images/" + user.emailAddress + ".jpg")
                val bitmap = (profilePicture.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val dataB = baos.toByteArray()

                val uploadTask = mountainsRef.putBytes(dataB)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    Log.d("UPLOAD", "Succes")
                }
            }
        }

        pencilButton.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startForResult.launch(gallery)
        }

        profilePicture.setOnClickListener {
            showPicture()
        }

        val ref = storage.reference.child("images/" + user.emailAddress + ".jpg")
        ref.downloadUrl.addOnSuccessListener { Uri ->
            imgURL = Uri.toString()
            Glide.with(requireActivity())
                .load(imgURL)
                .into(profilePicture)
        }

        buttonSave.setOnClickListener {
            val u  = User(user.userType, imgURL, userName.text.toString(), user.emailAddress, phoneNumber.text.toString(), datePickerText.text.toString(), user.className, user.classGrade, user.classGroup, user.teacherTimeTable)
            DatabaseManager.updateUserData(u)
            requireActivity().onBackPressed()
        }

        return view
    }

    override fun onResume() {
        if( !UtilsClass().isNetworkAvailable(requireContext()) ) {
            AlertDialogFragment().errorHandling(requireContext())
        }

        super.onResume()
    }

    private fun showPicture() {
        val alertAdd = AlertDialog.Builder(requireContext())
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.image_dialog, null)
        val img = view.findViewById<ImageView>(R.id.imageDialog)

        Glide.with(requireActivity())
            .load(imgURL)
            .into(img)
        alertAdd.setView(view)
        alertAdd.setNeutralButton("Bezár") { _, _ ->

        }

        alertAdd.show()
    }

    private fun showDatePickerDialog() {
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
        val sdf = SimpleDateFormat("yyyy.MMMM.dd")
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateString: String = sdf.format(calendar.time)
        datePickerText.setText(dateString)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imgURLToSend = context as OnPicHasChangedListener
    }
}

public interface OnPicHasChangedListener {
    fun onPicHasChanged (imgURL : String)
}