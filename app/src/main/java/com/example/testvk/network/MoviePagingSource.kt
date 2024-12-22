package com.example.testvk.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testvk.dataclasses.Genre
import com.example.testvk.dataclasses.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MoviePagingSource(
    private val genre: Genre? = null,
    private val apiService: ApiService,
    private val networkDelayMillis: Long
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> =
        withContext(Dispatchers.IO) {
            try {
                delay(networkDelayMillis)

                val nextPageNumber = params.key ?: 1
                val response = when {
                    genre != null -> apiService.getMoviesByGenre(nextPageNumber, genre = genre.name)

                    else -> apiService.getMovies(nextPageNumber)
                }

                return@withContext if (response.isSuccessful) {
                    val items = response.body()?.docs ?: emptyList()

                    LoadResult.Page(
                        data = items,
                        prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                        nextKey = if (items.isEmpty()) null else nextPageNumber + 1
                    )
                } else {
                    LoadResult.Error(Exception("Error fetching data"))
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition
}
