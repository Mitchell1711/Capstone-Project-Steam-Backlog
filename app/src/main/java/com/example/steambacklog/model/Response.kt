package com.example.steambacklog.model

import com.google.gson.annotations.SerializedName

data class Response (
    @SerializedName("response") val response : Library
)