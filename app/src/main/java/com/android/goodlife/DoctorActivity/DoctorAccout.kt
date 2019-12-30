package com.android.goodlife.DoctorActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.RadioButton

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.goodlife.MainActivityHome
import com.android.goodlife.Model.Doctor
import com.android.goodlife.R
import com.android.goodlife.UserActivity.SignIn
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_doctor_item.*
import kotlinx.android.synthetic.main.activity_doctor_item.profile_image
import kotlinx.android.synthetic.main.activity_take_photos.*
import java.util.*


class DoctorAccout : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_item)



        choose_images_doctor.setOnClickListener {

            val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iImg, 0)
        }


    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            profile_image.setImageBitmap(bitmap)

            choose_images_doctor.alpha = 0f


        }

        btn_save.setOnClickListener {

            val genderBtn = findViewById<RadioButton>(gender.getCheckedRadioButtonId())
            val nama = findViewById<TextInputEditText>(R.id.add_nama_doctor).text.toString()
            val email = findViewById<TextInputEditText>(R.id.add_email_doctor).text.toString()
            val pass = findViewById<TextInputEditText>(R.id.add_password_doctor).text.toString()
            val gender = genderBtn.getText().toString()

            if (nama.isEmpty() || email.isEmpty() || pass.isEmpty() || gender.isEmpty()){

                Toast.makeText(this, "Column cannot be empty", Toast.LENGTH_LONG).show()
                Log.d("Doctor Account","Column cannot be empty")
                return@setOnClickListener
            }else if (pass.length < 8){

                Toast.makeText(this, "please insert password min 8", Toast.LENGTH_LONG).show()
            }else{

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {

                        if (!it.isSuccessful){

                            Log.d("Doctor Account", "Your Registrasion fail")
                            return@addOnCompleteListener
                            Toast.makeText(this, "Please Check back Your Bio", Toast.LENGTH_LONG).show()

                        }else{

                            Log.d("DoctorAccount", "Its Successfully")

                            UploadImagestoFirebaseStorage()



                            Toast.makeText(this, "Anda Berhasil membuat Akun", Toast.LENGTH_LONG).show()


                        }
                    }




            }

        }

    }

    private fun UploadImagestoFirebaseStorage(){
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/Doctor/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("DoctorAccount", "Successfully save to Firebase Storage")
                    val photos = it.toString()
                    val name = add_nama_doctor.text.toString()
                    val email = add_email_doctor.text.toString()
                    val password = add_password_doctor.text.toString()
                    val genderBtn = findViewById<RadioButton>(gender.getCheckedRadioButtonId())
                    val gender = genderBtn.getText().toString()


                    val ref1 = FirebaseDatabase.getInstance().getReference("doctor")
                    val uid = FirebaseAuth.getInstance().uid.toString()

                    val doctor = Doctor(uid,name,email, password, gender,photos)

                    ref1.child(uid).setValue(doctor)
                        .addOnCompleteListener {

                            Log.d("DoctorAccount", "Succescfully add Data on database Firebase")
                            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()

                            val intent = Intent(this, MainActivityHome::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()

                        }
                        .addOnFailureListener {

                            return@addOnFailureListener

                            Log.d("DoctorAccount", "Failed add data in Database firebase")
                            Toast.makeText(this, "Saved its Fail", Toast.LENGTH_LONG).show()
                        }

                }

            }

    }


}