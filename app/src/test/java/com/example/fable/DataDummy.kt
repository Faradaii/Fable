package com.example.fable

import com.example.fable.data.local.entity.Story

object DataDummy {

    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "Photo $i",
                "${System.currentTimeMillis()}",
                "Story $i",
                "Description $i"
            )
            items.add(story)
        }
        return items
    }
}