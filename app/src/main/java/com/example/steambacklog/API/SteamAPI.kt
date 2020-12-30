package com.example.steambacklog.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SteamAPI {
    companion object {
        // The base url off the api.
        private const val baseUrl = "http://api.steampowered.com/"

        /**
         * @return [SteamAPIService] The service class off the retrofit client.
         */
        fun createApi(): SteamAPIService {
            // Create an OkHttpClient to be able to make a log of the network traffic
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            // Create the Retrofit instance
            val steamAPI = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Return the Retrofit NumbersApiService
            return steamAPI.create(SteamAPIService::class.java)
        }
    }
}