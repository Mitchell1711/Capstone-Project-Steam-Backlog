package com.example.steambacklog.model

import com.google.gson.annotations.SerializedName

data class Games(
    @SerializedName("appid") val appid : Int,
    @SerializedName("name") val name : String,
    @SerializedName("playtime_forever") val playtime_forever : Int,
    @SerializedName("img_icon_url") val img_icon_url : String,
    @SerializedName("img_logo_url") val img_logo_url : String,
    @SerializedName("has_community_visible_stats") val has_community_visible_stats : Boolean,
    @SerializedName("playtime_windows_forever") val playtime_windows_forever : Int,
    @SerializedName("playtime_mac_forever") val playtime_mac_forever : Int,
    @SerializedName("playtime_linux_forever") val playtime_linux_forever : Int,

    var completion_Status: Completion,
    var note: String
)