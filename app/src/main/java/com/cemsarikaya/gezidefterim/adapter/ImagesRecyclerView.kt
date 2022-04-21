package com.cemsarikaya.gezidefterim.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cemsarikaya.gezidefterim.databinding.ImagesRowBinding
import com.cemsarikaya.gezidefterim.model.Posts
import com.cemsarikaya.gezidefterim.view.FullScreenImageActivity
import android.content.Context
import com.bumptech.glide.Glide






class ImagesRecyclerView(val postArrayList: ArrayList<Posts>,private val context: Context): RecyclerView.Adapter<ImagesRecyclerView.ViewHolder>() {

    class ViewHolder(val binding: ImagesRowBinding) : RecyclerView.ViewHolder(binding.root) {


    }



      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          val binding = ImagesRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
          return  ViewHolder(binding)
      }

      override fun onBindViewHolder(holder: ViewHolder, position: Int) {


          Glide.with(context)
              .load(postArrayList.get(position).downloadUrl)
              .centerCrop()
              .into(holder.binding.recyclerImageView)




          holder.binding.recyclerImageView.setOnClickListener {

              val intent = Intent(holder.itemView.context, FullScreenImageActivity::class.java)
              intent.putExtra("image_url", postArrayList.get(position).downloadUrl)
              holder.itemView.context.startActivity(intent)




          }
      }




      override fun getItemCount(): Int {

          return postArrayList.size
      }



}
