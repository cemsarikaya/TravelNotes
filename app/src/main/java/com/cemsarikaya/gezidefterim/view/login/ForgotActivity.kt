package com.cemsarikaya.gezidefterim.view.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cemsarikaya.gezidefterim.R
import com.cemsarikaya.gezidefterim.databinding.ActivityForgotBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth
private lateinit var binding: ActivityForgotBinding
class ForgotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

    }
   fun resetPassword(view: View){
       val email : String = binding.EmailText.text.toString().trim{it <= ' '}

       if (email.equals("")){
           Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show()

       }else
       {
           FirebaseAuth.getInstance().sendPasswordResetEmail(email)
               .addOnCompleteListener{task ->
                   if(task.isSuccessful){
                       Toast.makeText(this, "Email send successfully to reset your password!", Toast.LENGTH_LONG).show()
                       overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                       finish()

                   }else{

                       Toast.makeText(this,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                   }
               }
       }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        return super.onSupportNavigateUp()

    }
}