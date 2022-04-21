package com.cemsarikaya.gezidefterim.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.cemsarikaya.gezidefterim.databinding.ActivityFullScreenImageBinding
import android.view.View

import com.github.chrisbanes.photoview.PhotoView

import java.io.*
import java.util.*


class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val image = intent.getStringExtra("image_url")

        val photoView = findViewById<View>(binding.photoView.id) as PhotoView

        Glide.with(this).load(image).into(photoView)



    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(com.cemsarikaya.gezidefterim.R.anim.slide_from_left, com.cemsarikaya.gezidefterim.R.anim.slide_to_right)
        return super.onSupportNavigateUp()

    }


}




