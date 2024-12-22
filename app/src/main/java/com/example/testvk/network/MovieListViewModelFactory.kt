package com.example.testvk.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testvk.movielist.MovieListViewModel
import com.example.testvk.moviescreen.MovieScreenViewModel

class MovieListViewModelFactory(
    private val repository: MovieRepository,
    private val additionalIntValue: Int? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MovieScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieScreenViewModel(repository, additionalIntValue ?: 0) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
