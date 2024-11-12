package com.example.fable.data.remote.response

import com.example.fable.data.local.entity.Story
import com.google.gson.annotations.SerializedName

data class GetDetailResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: Story? = null
)

