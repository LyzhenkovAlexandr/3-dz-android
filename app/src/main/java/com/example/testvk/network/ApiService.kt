package com.example.testvk.network

import com.example.testvk.dataclasses.Genre
import com.example.testvk.dataclasses.Movie
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("v1.4/movie")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("sortField") sortField: String = "votes.kp",
        @Query("sortField") sortField1: String = "rating.kp",
        @Query("sortType") sortType: Int = -1,
        @Query("sortType") sortType1: Int = -1,
        @Query("token") apiKey: String = TOKEN
    ): Response<MovieResponse>

    @GET("v1.4/movie")
    suspend fun getMoviesByGenre(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("sortField") sortField: String = "votes.kp",
        @Query("sortField") sortField1: String = "rating.kp",
        @Query("sortType") sortType: Int = -1,
        @Query("sortType") sortType1: Int = -1,
        @Query("genres.name") genre: String,
        @Query("token") apiKey: String = TOKEN
    ): Response<MovieResponse>

    @GET("v1.4/movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("token") apiKey: String = TOKEN
    ): Response<Movie>

    @GET("v1/movie/possible-values-by-field")
    suspend fun getGenres(
        @Query("field") genres: String = "genres.name",
        @Query("token") apiKey: String = TOKEN
    ): Response<List<Genre>>

    companion object {
        private const val TOKEN = "56PJR4X-SG5M5K8-QX04GKP-SPFN41S"
        fun create(): ApiService =
            Retrofit.Builder()
                .baseUrl("https://api.kinopoisk.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
    }

    object NetworkConfig {
        var networkDelayMillis: Long = 2000
    }
}
