package com.example.lifeafterdorm.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.R
import com.example.lifeafterdorm.data.RentalRoom
import com.bumptech.glide.Glide
import android.content.Context
import com.example.lifeafterdorm.data.User

class ListAdapter (private val context: Context, private val rentalRoomList: List<RentalRoom>) : RecyclerView.Adapter <ListAdapter.MyViewHolder>(){

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvPostManagementTitle : TextView = itemView.findViewById(R.id.tvPostManagementTitle)
        val tvPostManagementPostTime : TextView = itemView.findViewById(R.id.tvPostManagementPostTime)
        val imageViewPostManagement : ImageView = itemView.findViewById(R.id.imageViewPostManagement)
        val imageButtonDelete : ImageButton = itemView.findViewById(R.id.imageButtonDelete)
//        val imageButtonEdit : ImageButton = itemView.findViewById(R.id.imageButtonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rental_management_view_holder, parent, false )

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rentalRoomList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = rentalRoomList[position]

        holder.tvPostManagementTitle.text = currentItem.title
        holder.tvPostManagementPostTime.text = currentItem.editTextTextMultiLineDesc
        Glide.with(context).load(currentItem.imageUri).into(holder.imageViewPostManagement)

        holder.imageButtonDelete.setOnClickListener {
            onDeleteClickListener?.onDeleteClick(position)
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }
    private var onDeleteClickListener: OnDeleteClickListener? = null

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

}