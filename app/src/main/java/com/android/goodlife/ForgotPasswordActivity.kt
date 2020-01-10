package com.android.goodlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {


    var auth = FirebaseAuth.getInstance()
    val TAG = "ForgotPassword"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        btn_forgot_password.setOnClickListener {


            val mail = edt_emailforgot.text.toString().trim()

            if (TextUtils.isEmpty(mail)){

                Log.d(TAG, "Entry Your Email Address")
                Toast.makeText(this, "Masukkan Email Anda ",
                    Toast.LENGTH_SHORT).show()
            }else{

                auth!!.sendPasswordResetEmail(mail)
                    .addOnCompleteListener {
                        if (it.isSuccessful){

                            Log.d(TAG, "Check email to reset your Password")
                            Toast.makeText(this, "Silahkan check email anda untuk mereset ulang password anda",
                                Toast.LENGTH_SHORT).show()

                        }else{

                            Log.d(TAG, "Failed to Reset Your Password email!")
                            Toast.makeText(this, "Anda gagal mengubah password email, silahkan ulang kembali",
                                Toast.LENGTH_SHORT).show()


                        }
                        if (!it.isSuccessful){

                            Log.d(TAG, "Authentication is Failed!")
                            Toast.makeText(this, "Gagal menAuthenticasi password email!",
                                Toast.LENGTH_SHORT).show()
                        }

                    }

            }

        }


    }
}
