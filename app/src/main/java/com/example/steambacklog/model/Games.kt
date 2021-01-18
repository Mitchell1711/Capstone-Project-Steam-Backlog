package com.example.steambacklog.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
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
) : Parcelable {
    fun getIconUrl() =
        "http://media.steampowered.com/steamcommunity/public/images/apps/$appid/$img_icon_url.jpg"

    fun getLogoUrl() =
        "http://media.steampowered.com/steamcommunity/public/images/apps/$appid/$img_logo_url.jpg"
}