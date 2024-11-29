package com.example.lifeafterdorm.dataAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.R
import com.example.lifeafterdorm.RoomDetailsActivity
import com.example.lifeafterdorm.data.CommunityPost
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.data.User
import de.hdodenhof.circleimageview.CircleImageView

class CommunityPostAdapter (private val context: Context, private val communityPostList: List<CommunityPost>, private val userList: MutableMap<String, User>, private val roomList: MutableMap<String, RentalRoom>) : RecyclerView.Adapter <CommunityPostAdapter.MyViewHolder>(){

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val idCVCommunityPost : CircleImageView = itemView.findViewById(R.id.idCVCommunityPost)
        val idTVCommunityPostName : TextView = itemView.findViewById(R.id.idTVCommunityPostName)
        val idTVCommunityPostTime : TextView = itemView.findViewById(R.id.idTVCommunityPostTime)
        val idTVCommunityPostDescription : TextView = itemView.findViewById(R.id.idTVCommunityPostDescription)
        val idIVPost : ImageView = itemView.findViewById(R.id.idIVPost)
        val linearLayoutCommunityPostContactWhatsapp : LinearLayout = itemView.findViewById(R.id.linearLayoutCommunityPostContactWhatsapp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.community_post_view_holder, parent, false )

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return communityPostList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = communityPostList[position]
        val currentUserItem = userList[currentItem.userId]
        val currentRentalRoomItem = roomList[currentItem.roomId]

        holder.idTVCommunityPostName.text = currentUserItem?.name
        holder.idTVCommunityPostTime.text = currentItem.postDateTime
        holder.idTVCommunityPostDescription.text = currentItem.postContent
        Glide.with(context).load(currentRentalRoomItem?.imageUri).into(holder.idIVPost)

        holder.idIVPost.setOnClickListener() {
            val intent = Intent(context, RoomDetailsActivity::class.java)
            intent.putExtra("roomId", currentRentalRoomItem?.roomId)
            context.startActivity(intent)
        }

        holder.linearLayoutCommunityPostContactWhatsapp.setOnClickListener {

        }
    }
}