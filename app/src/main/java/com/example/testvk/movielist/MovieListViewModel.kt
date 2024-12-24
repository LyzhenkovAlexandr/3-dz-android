package com.example.testvk.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.example.testvk.dataclasses.Genre
import com.example.testvk.dataclasses.Movie
import com.example.testvk.network.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieListViewModel(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> get() = _movies

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> get() = _genres

    private val _selectedMovieId = MutableLiveData<Int?>()
    val selectedMovieId: LiveData<Int?> get() = _selectedMovieId

    private var currentPagingSource: PagingSource<Int, Movie>? = null


    init {
        fetchGenres()
        fetchMovies()
    }

    private fun fetchMovies(genre: Genre? = null) {
        currentPagingSource?.invalidate()
        currentPagingSource = null

        viewModelScope.launch {
            _movies.postValue(PagingData.empty())
        }

        val pager = Pager(PagingConfig(pageSize = 10)) {
            currentPagingSource?.takeIf { it.invalid.not() }
                ?: if (genre == null) {
                    repository.getMoviesPagingSource()
                } else {
                    repository.getMoviesByGenrePagingSource(genre)
                }.also {
                    currentPagingSource = it
                }
        }

        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _movies.postValue(it)
            }
        }
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = repository.getGenres()
                    if (response.isSuccessful) {
                        _genres.postValue(response.body())
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun retry() {
        fetchGenres()
        currentPagingSource?.invalidate()
    }

    fun resetSelectedMovieId() {
        _selectedMovieId.value = null
    }

    fun selectMovie(movieId: Int) {
        _selectedMovieId.value = movieId
    }

    fun chooseGenre (genre: Genre) {
        fetchMovies(genre)
    }
}
