package com.android.goodlife.UserActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.goodlife.MainActivityHomeCustomers
import com.android.goodlife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_take_photos.*
import java.util.*

class TakePhoto : AppCompatActivity() {

    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photos)

        ref = FirebaseDatabase.getInstance().getReference("users")


        choose_image.setOnClickListener {

            val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iImg, 0)
        }

        fabtake.setOnClickListener {
            uploadImageToFirbaseStorage()


        }

    }



    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            profile_image.setImageBitmap(bitmap)

            choose_image.alpha = 0f


        }
    }



    private fun uploadImageToFirbaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/users/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val photo = it.toString()
                    saveImagesToFirebaseDatabase(photo)
                }
            }
    }

    private fun saveImagesToFirebaseDatabase(photo: String){

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseUser?.uid

        ref.child(userId!!).ref.child("photo").setValue(photo)

      Toast.makeText(this, "Add photo Successfully", Toast.LENGTH_LONG).show()
        Log.d("Take Photo", "Successfully Add photo")



        val intent = Intent(this, MainActivityHomeCustomers::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()


    }
}

