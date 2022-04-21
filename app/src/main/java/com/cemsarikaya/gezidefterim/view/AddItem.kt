package com.cemsarikaya.gezidefterim.view
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cemsarikaya.gezidefterim.R
import com.cemsarikaya.gezidefterim.databinding.ActivityAddItemBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList


class AddItem : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private lateinit var activityResultLauncer: ActivityResultLauncher<Intent>
    private lateinit var permissionLAuncher: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var postion = 0
    private var mArrayUri : ArrayList<Uri>?=null
    private  var up = 0
    private  var k = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mArrayUri = ArrayList()
        registerLauncher()
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.next.setOnClickListener {
            if (postion < mArrayUri!!.size - 1) {
                postion++
                binding.imageView.setImageURI (mArrayUri!![postion])

            }
        }
        binding.back.setOnClickListener {

            if (postion > 0) {
                postion--
                binding.imageView.setImageURI(mArrayUri!![postion])
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        return super.onSupportNavigateUp()

    }

    fun saveButton(view: View) {
        val latitude = intent.getDoubleExtra("latitude", Double.NaN)
        val longitude = intent.getDoubleExtra("longitude", Double.NaN)

        val LatLng = LatLng(latitude,longitude)

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

    if(mArrayUri.isNullOrEmpty()){
    Toast.makeText(this, "Please Select Picture!", Toast.LENGTH_SHORT).show()
        }

        if(mArrayUri != null && binding.titleText.text.isNotEmpty() && binding.countryText.text.isNotEmpty() && binding.noteText.text.isNotEmpty()) {

                val reference = storage.reference
                while (up < mArrayUri!!.size) {

                    binding.progressBar.visibility = View.VISIBLE
                   val imagesReference = reference.child(imageName).child(mArrayUri!![k].lastPathSegment!!)
                    imagesReference.putFile(mArrayUri!![k]).addOnSuccessListener { taskSnapshot ->

                        imagesReference.downloadUrl.addOnSuccessListener { uri ->

                          val  downloadUrl = uri.toString()

                            val postMap = hashMapOf<String, Any>()
                            postMap.put("downloadUrl", downloadUrl)
                            postMap.put("userEmail", auth.currentUser!!.email.toString())
                            postMap.put("title", binding.titleText.text.toString())
                            postMap.put("country", binding.countryText.text.toString())
                            postMap.put("note", binding.noteText.text.toString())
                            postMap.put("latitude", latitude)
                            postMap.put("longitude", longitude)
                            postMap.put("LatLng", LatLng)



                            firestore.collection("Posts").add(postMap)
                                .addOnCompleteListener { task ->
                                    if (task.isComplete && task.isSuccessful) {
                                        //back
                                        finish()

                                    }
                                }

                        }.addOnFailureListener {
                            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    k++
                    up++


                }

            } else  {
                Toast.makeText(this, "Do not leave blank!", Toast.LENGTH_SHORT).show()
            }


        }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission", View.OnClickListener {
                        permissionLAuncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()

            } else {
                permissionLAuncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        } else {

            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_PICK
           activityResultLauncer.launch(intent)

        }
    }
    private fun registerLauncher() {
        activityResultLauncer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    if (result.data!!.clipData != null) {

                        val count = result.data!!.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = result.data!!.clipData!!.getItemAt(i).uri
                            mArrayUri!!.add(imageUri)
                        }
                        binding.imageView.setImageURI(mArrayUri!![0])
                        postion = 0
                    } else {
                        val imageUri = result.data!!.data
                        binding.imageView.setImageURI(imageUri)
                        mArrayUri!!.add(imageUri!!)
                        postion = 0
                    }
                }

            }
        permissionLAuncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {

                    val intent = Intent()
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.action = Intent.ACTION_PICK
                    activityResultLauncer.launch(intent)


                } else {
                    Toast.makeText(this@AddItem, "Permission needed!", Toast.LENGTH_LONG).show()
                }
            }
    }
}












