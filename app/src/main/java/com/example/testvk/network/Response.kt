package com.example.testvk.network

import com.example.testvk.dataclasses.Genre
import com.example.testvk.dataclasses.Movie

data class MovieResponse(
    val docs: List<Movie>
)

data class GenreResponse(
    val array: List<Genre>
)
