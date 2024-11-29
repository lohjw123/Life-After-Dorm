package com.example.lifeafterdorm.dataAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.R
import com.example.lifeafterdorm.RoomDetailsActivity
import com.example.lifeafterdorm.data.RentalRoom

class SearchAdapter(private val context: Context, private var dataList: List<RentalRoom>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_layout_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(dataList[position].imageUri).into(holder.image)
        holder.title.text = dataList[position].title

        holder.relativeLayoutSearch.setOnClickListener() {
            val intent = Intent(context, RoomDetailsActivity::class.java)
            intent.putExtra("roomId", dataList[position].roomId)
            context.startActivity(intent)
        }
    }

    fun searchDataList(searchList: List<RentalRoom>){
        dataList = searchList
        notifyDataSetChanged()
    }

}

class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var image: ImageView
    var title: TextView
    init {
        image = itemView.findViewById(R.id.image)
        title = itemView.findViewById(R.id.title)
    }
    var relativeLayoutSearch : RelativeLayout = itemView.findViewById(R.id.relativeLayoutSearch)
}