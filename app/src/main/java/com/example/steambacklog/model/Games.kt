package com.example.steambacklog.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Games(
    @ColumnInfo(name = "appid") @SerializedName("appid") val appid : Int,
    @ColumnInfo(name = "name") @SerializedName("name") val name : String,
    @ColumnInfo(name = "playtime_forever") @SerializedName("playtime_forever") val playtime_forever : Int,
    @ColumnInfo(name = "img_icon_url") @SerializedName("img_icon_url") val img_icon_url : String,
    @ColumnInfo(name = "img_logo_url") @SerializedName("img_logo_url") val img_logo_url : String,
    @ColumnInfo(name = "has_community_visible_stats") @SerializedName("has_community_visible_stats") val has_community_visible_stats : Boolean,
    @ColumnInfo(name = "playtime_windows_forever") @SerializedName("playtime_windows_forever") val playtime_windows_forever : Int,
    @ColumnInfo(name = "playtime_mac_forever") @SerializedName("playtime_mac_forever") val playtime_mac_forever : Int,
    @ColumnInfo(name = "playtime_linux_forever") @SerializedName("playtime_linux_forever") val playtime_linux_forever : Int,
) : Parcelable {
    fun getIconUrl() =
        "http://media.steampowered.com/steamcommunity/public/images/apps/$appid/$img_icon_url.jpg"

    fun getLogoUrl() =
        "http://media.steampowered.com/steamcommunity/public/images/apps/$appid/$img_logo_url.jpg"
}