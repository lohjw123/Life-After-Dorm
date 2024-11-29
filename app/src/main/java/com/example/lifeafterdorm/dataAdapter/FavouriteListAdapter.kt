package com.example.lifeafterdorm.dataAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.R
import com.example.lifeafterdorm.RoomDetailsActivity
import com.example.lifeafterdorm.data.RentalRoom

class FavouriteListAdapter (private val context: Context, private val rentalRoomList: List<RentalRoom>) : RecyclerView.Adapter <FavouriteListAdapter.MyViewHolder>(){

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val idTVFavouriteListTitle : TextView = itemView.findViewById(R.id.idTVFavouriteListTitle)
        val idTVFavouriteRoomType : TextView = itemView.findViewById(R.id.idTVFavouriteRoomType)
        val idTVFavouriteListPrice : TextView = itemView.findViewById(R.id.idTVFavouriteListPrice)
        val idIVFavouriteListImage : ImageView = itemView.findViewById(R.id.idIVFavouriteListImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favourite_list_view_holder, parent, false )

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rentalRoomList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = rentalRoomList[position]

        holder.idTVFavouriteListTitle.text = currentItem.title
        holder.idTVFavouriteRoomType.text = currentItem.roomType1
        holder.idTVFavouriteListPrice.text = "RM" + currentItem.monthlyRental
        Glide.with(context).load(currentItem.imageUri).into(holder.idIVFavouriteListImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RoomDetailsActivity::class.java)
            intent.putExtra("roomId", rentalRoomList[position].roomId)
            context.startActivity(intent)
        }
    }
}