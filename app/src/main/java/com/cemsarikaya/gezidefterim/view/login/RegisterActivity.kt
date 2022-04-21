package com.cemsarikaya.gezidefterim.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cemsarikaya.gezidefterim.R

import com.cemsarikaya.gezidefterim.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityRegisterBinding
private lateinit var auth: FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


    }


    fun registerButton(view:View){

        val email = binding.EmailText.text.toString()
        val password = binding.passwordText.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty()){

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{

                Toast.makeText(this@RegisterActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()

            }
        }else{

            Toast.makeText(this, "Enter Email and Password!", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        return super.onSupportNavigateUp()

    }


}
