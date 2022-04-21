package com.cemsarikaya.gezidefterim.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cemsarikaya.gezidefterim.R
import com.cemsarikaya.gezidefterim.adapter.ImagesRecyclerView
import com.cemsarikaya.gezidefterim.databinding.ActivityDetailsBinding
import com.cemsarikaya.gezidefterim.model.Posts
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList

class DetailsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Posts>
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imagesAdapter : ImagesRecyclerView

    private lateinit var binding: ActivityDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

        postArrayList = ArrayList()
        getData()

        val gridLayout = GridLayoutManager(this,2)
        imagesAdapter = ImagesRecyclerView(postArrayList,this)


        binding.recyclerView.layoutManager = gridLayout
        binding.recyclerView.adapter = imagesAdapter




    }

    fun deleteButton(view: View) {
     /*

          val storageRef = storage.reference

          val desertRef = storageRef.child("$imageUrl")


          desertRef.delete().addOnSuccessListener {
              val intent = Intent(this,MapsActivity::class.java)
              startActivity(intent)
              overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
          }

      */


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->

                    val bundle = intent.getParcelableExtra<Bundle>("bundle")
                    val ss: LatLng? = bundle!!.getParcelable("ss")
                    db.collection("Posts")
                        .whereEqualTo("LatLng",ss)
                        .get()
                        .addOnSuccessListener { queryDocumentSnapshots ->
                            val batch = db.batch()
                            val snapshotList = queryDocumentSnapshots.documents
                            for (snapshot in snapshotList) {
                                batch.delete(snapshot.reference)
                            }
                            batch.commit()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                        }

                    val intent = Intent(this,MapsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    finish()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun getData() {

        val bundle = intent.getParcelableExtra<Bundle>("bundle")
        val ss: LatLng? = bundle!!.getParcelable("ss")
        db.collection("Posts").whereEqualTo("LatLng",ss)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()

                } else {

                    if (value != null) {
                        if (!value.isEmpty) {

                            val documents = value.documents

                            postArrayList.clear()
                            for (document in documents) {

                                val country = document.get("country") as String
                                val title = document.get("title") as String
                                val note = document.get("note") as String
                                val userEmail = document.get("userEmail") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                val latitude = document.get("latitude") as Double
                                val longitude = document.get("longitude") as Double
                                val post = Posts(country,downloadUrl,latitude,longitude,note,title,userEmail)

                                postArrayList.add(post)
                                binding.titleDetailTextView.text = title
                                binding.noteTextView.text = note
                                binding.countryTextView.text = country

                                }
                            }
                            imagesAdapter.notifyDataSetChanged()
                        }

                        else{
                        Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
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





