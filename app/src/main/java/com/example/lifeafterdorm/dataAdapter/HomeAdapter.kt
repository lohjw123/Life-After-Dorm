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
import androidx.cardview.widget.CardView
import com.example.lifeafterdorm.RoomDetailsActivity

class HomeAdapter (private val context: Context, private val rentalRoomList: List<RentalRoom>) : RecyclerView.Adapter <HomeAdapter.MyViewHolder>(){

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvHomeTitle : TextView = itemView.findViewById(R.id.tvHomeTitle)
        val tvHomePopularTitle : TextView = itemView.findViewById(R.id.tvHomePopularTitle)
        val tvHomeLocation : TextView = itemView.findViewById(R.id.tvHomeRecommendTitle)
        val tvHomePrice : TextView = itemView.findViewById(R.id.tvHomePrice)
        val imageViewHomeImage : ImageView = itemView.findViewById(R.id.imageViewHomeImage)
        val cardViewRecommend : CardView = itemView.findViewById(R.id.cardViewRecommend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_page_view_holder, parent, false )

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rentalRoomList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = rentalRoomList.getOrNull(position) ?: return

        holder.tvHomeTitle.text = currentItem.roomType1
        holder.tvHomePopularTitle.text = currentItem.title
        holder.tvHomeLocation.text = currentItem.location
        holder.tvHomePrice.text = "RM " + currentItem.monthlyRental
        Glide.with(context).load(currentItem.imageUri).into(holder.imageViewHomeImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RoomDetailsActivity::class.java)
            intent.putExtra("contactNum", rentalRoomList[position].contactNum)
            intent.putExtra("roomId", rentalRoomList[position].roomId)
            intent.putExtra("userId", rentalRoomList[position].userId)
            context.startActivity(intent)
        }
    }

//    private var ItemClickListener: OnItemClickListener? = null
//
//    interface OnItemClickListener {
//        fun onItemClick(rentalRoom: RentalRoom)
//    }
}