package com.example.fable.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.R
import com.example.fable.data.local.entity.Story
import com.example.fable.databinding.StoryItemBinding
import com.example.fable.view.detail.DetailActivity

class StoryItemAdapter: ListAdapter<Story, StoryItemAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listItem: Story){
            binding.apply {
                Glide.with(root.context)
                    .load(listItem.photoUrl)
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_image_24)
                    .into(ivStoryImage)

                Glide.with(root.context)
                    .load("https://avatar.iran.liara.run/public")
                    .signature(ObjectKey(listItem.id.toString()))
                    .placeholder(R.drawable.resource_public)
                    .error(R.drawable.resource_public)
                    .into(ivAvatarRandom)

                tvStoryName.text = listItem.name
                tvStoryDesc.text = listItem.description

                itemView.setOnClickListener {
                    val intent = Intent(it.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_ID, listItem.id)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivStoryImage, "iv_story_image"),
                            Pair(ivAvatarRandom, "iv_story_avatar"),
                            Pair(tvStoryName, "tv_story_name"),
                            Pair(tvStoryDesc, "tv_story_desc"),
                        )
                    it.context.startActivity(intent, optionsCompat.toBundle())
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