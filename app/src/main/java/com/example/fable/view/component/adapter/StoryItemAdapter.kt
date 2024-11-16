package com.example.fable.view.component.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.BuildConfig
import com.example.fable.data.local.entity.Story
import com.example.fable.databinding.StoryItemBinding
import com.example.fable.view.component.myImageView.ImageView.loadImage
import com.example.fable.view.detail.DetailActivity

class StoryItemAdapter : PagingDataAdapter<Story, StoryItemAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItem(position) == null) return
        val item = getItem(position)
        holder.bind(item!!)
    }

    class ViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listItem: Story){
            binding.apply {
                ivItemPhoto.loadImage(
                    root.context,
                    listItem.photoUrl,
                )
                ivAvatarRandom.loadImage(
                    root.context,
                    BuildConfig.BASE_URL_RANDOM_AVATAR,
                    signature = ObjectKey(listItem.name)
                )
                tvItemName.text = listItem.name
                tvStoryDesc.text = listItem.description

                itemView.setOnClickListener {
                    val intent = Intent(it.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_ID, listItem.id)
                    itemView.context.startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                            .toBundle()
                    )
                }
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}