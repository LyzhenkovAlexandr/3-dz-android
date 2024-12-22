package com.example.testvk.network

import android.util.Log
import androidx.paging.PagingSource
import com.example.testvk.dataclasses.Genre
import com.example.testvk.dataclasses.Movie
import kotlinx.coroutines.delay
import retrofit2.Response

class MovieRepository(private val apiService: ApiService, private val networkDelayMillis: Long) {

    suspend fun getMovie(id: Int): Response<Movie> {
        delay(networkDelayMillis)
        return apiService.getMovie(id)
    }

    suspend fun getGenres(): Response<List<Genre>> {
        delay(networkDelayMillis)
        return apiService.getGenres()
    }

    fun getMoviesByGenrePagingSource(genre: Genre): PagingSource<Int, Movie> {
        Log.e("getMoviesByGenrePagingSource", genre.toString())
        return MoviePagingSource(genre = genre, apiService, networkDelayMillis)
    }

    fun getMoviesPagingSource(): PagingSource<Int, Movie> =
        MoviePagingSource(apiService = apiService, networkDelayMillis = networkDelayMillis)
}
