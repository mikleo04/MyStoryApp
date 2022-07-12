package com.example.mystoryapp.ui.story.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.api.response.ListStoryItem
import com.example.mystoryapp.databinding.RvItemRowsBinding

class StoryAdapter(private val storyList: ArrayList<ListStoryItem>): RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(RvItemRowsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = storyList[position]
        holder.binding.rvItemRowName.text = data.name

        Glide.with(holder.binding.root.context)
            .load(data.photoUrl)
            .into(holder.binding.rvItemRowImgBanner)

        holder.itemView.setOnClickListener{onItemClickCallback.onItemClicked(storyList[holder.adapterPosition])}
    }
    
    override fun getItemCount(): Int = storyList.size

    fun notifyDatasetChangedHelper(){
        notifyDataSetChanged()
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    class ListViewHolder(var binding: RvItemRowsBinding) : RecyclerView.ViewHolder(binding.root)
}