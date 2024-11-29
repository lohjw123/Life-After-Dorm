package com.example.lifeafterdorm.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.R
import com.example.lifeafterdorm.data.RentalRoom
import com.bumptech.glide.Glide
import android.content.Context
import android.content.Intent
import com.example.lifeafterdorm.RoomDetailsActivity

class HomeRecommendAdapter (private val context: Context, private val rentalRoomList: List<RentalRoom>) : RecyclerView.Adapter <HomeRecommendAdapter.MyViewHolder>(){

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvHomeRecommendTitle : TextView = itemView.findViewById(R.id.tvHomeRecommendTitle)
        val imageViewHomeRecommendImage : ImageView = itemView.findViewById(R.id.imageViewHomeRecommendImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_page_recommend_view_holder, parent, false )

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rentalRoomList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = rentalRoomList.getOrNull(position) ?: return

        holder.tvHomeRecommendTitle.text = currentItem.roomType1
        Glide.with(context).load(currentItem.imageUri).into(holder.imageViewHomeRecommendImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RoomDetailsActivity::class.java)
            intent.putExtra("contactNum", rentalRoomList[position].contactNum)
            intent.putExtra("roomId", rentalRoomList[position].roomId)
            intent.putExtra("userId", rentalRoomList[position].userId)
            context.startActivity(intent)
        }
    }
}