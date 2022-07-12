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
        val binding = RvItemRowsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = storyList[position]
        holder.binding.rvItemRowStory.text = data.description
        holder.binding.rvItemRowName.text = data.name
        holder.binding.rvItemRowDateCreated.text = data.createdAt.toString().substring(0,10)

        Glide.with(holder.binding.root.context)
            .load(data.photoUrl)
            .into(holder.binding.rvItemRowImgBanner)

        // check if night mode activated
//        if ( holder.binding.root.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
//            holder.binding.root.setCardBackgroundColor(Color.TRANSPARENT)

        holder.itemView.setOnClickListener{onItemClickCallback.onItemClicked(storyList[holder.adapterPosition])}
    }

    //    override fun getItemCount(): Int = storyList.size
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