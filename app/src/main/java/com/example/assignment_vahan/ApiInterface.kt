package com.example.assignment_vahan

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("/search")
    fun getData(): Call<ArrayList<Items>>
}