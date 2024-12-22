package com.example.testvk.movielist

import android.app.Application
import com.example.testvk.network.ApiService
import com.squareup.picasso.Picasso

class Application : Application() {
    private lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()

        apiService = ApiService.create()

        val picasso = Picasso.Builder(this)
            .build()

        Picasso.setSingletonInstance(picasso)
    }
}
