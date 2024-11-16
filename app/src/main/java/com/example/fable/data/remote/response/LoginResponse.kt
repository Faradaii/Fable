package com.example.fable.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
	@field:SerializedName("loginResult")
	val loginResult: LoginResult? = null,
)