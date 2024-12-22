package com.example.testvk.moviescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testvk.dataclasses.Movie
import com.example.testvk.network.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieScreenViewModel(
    private val repository: MovieRepository,
    private val request: Int?
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie> get() = _movie

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        request?.let {
            fetchMovie(it)
        }
    }

    private fun fetchMovie(id: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = repository.getMovie(id)
                    if (response.isSuccessful) {
                        _movie.postValue(response.body())
                    } else {
                        _error.postValue("Failed to fetch movie: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _error.postValue("Error occurred: ${e.message}")
            }
        }
    }

    fun reloadMovie(id: Int) {
        _error.postValue(null)
        fetchMovie(id)
    }
}
