package com.android.goodlife

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.android.goodlife.Fragment.TimelineFeedFragmentCust
import com.android.goodlife.Model.Timeline
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_timeline_doctor.*
import kotlinx.android.synthetic.main.activity_take_photos.*
import java.util.*
import kotlin.collections.HashMap

class AddTimelineDoctor : AppCompatActivity(){

    var selectedPhotoUri: Uri? = null
    private lateinit var btn_savestatus : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timeline_doctor)

        val imagesstatus = findViewById<Button>(R.id.choose_images)

        imagesstatus.setOnClickListener {
            val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iImg, 0)
        }

        btn_savestatus = findViewById(R.id.btn_savestatus)
        btn_savestatus.setOnClickListener {
            savePhotosInFirebaseStorage()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            images.setImageBitmap(bitmap)
        }
    }

    private fun savePhotosInFirebaseStorage() {
        if (selectedPhotoUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val filename = UUID.randomUUID().toString()
            val users = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val storage = FirebaseStorage.getInstance().getReference("/timelinePhotos/$users/$filename")

            storage.putFile(selectedPhotoUri!!)
                .addOnProgressListener { taskSnapshot ->

                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
                .addOnSuccessListener {
                    storage.downloadUrl.addOnSuccessListener {
                        progressDialog.dismiss()
                        Log.d(
                            "AddTimelineDoctor",
                            "Successfully save photos On FirebaseStorange : $users "
                        )
                        val imagesTimeline = it.toString()
                        saveImagesToFirebaseDatabase(imagesTimeline)
                    }
                }
        }else{
            Toast.makeText(this, "Gambarnya ga masuk deh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImagesToFirebaseDatabase(imagesTimeline: String) {

        val ref = FirebaseDatabase.getInstance().getReference("Timeline")
        val uploader = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val reference = FirebaseDatabase.getInstance().getReference("doctor")
        reference.child(uploader).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val photo = p0.child("photos").value.toString()
                    val name = p0.child("name").value.toString()
                    val status = add_deskripsi.text.toString()
                    val id = ref.push().key.toString()

                    val data = HashMap<String, String>()
                    data["id"] = id
                    data["imagesProfile"] = photo
                    data["imagesStatus"] = imagesTimeline
                    data["namaDokter"] = name
                    data["status"] = status

                    ref.child(id).setValue(data).addOnCompleteListener {
                        Log.d("AddTimeline", "Successfully add Stastus")
                        Toast.makeText(this@AddTimelineDoctor, "Successfully Upload", Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }

                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

}
