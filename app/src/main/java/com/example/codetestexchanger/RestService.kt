package com.example.codetestexchanger

import com.example.codetestexchanger.dataclass.RestData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestService {

    @GET("/currency_data/live")
    suspend fun requestRest(@Query("apikey") apikey:String,
                            @Query("source")source: String,
                            @Query("currencies") currencies : String):Response<RestData>
}