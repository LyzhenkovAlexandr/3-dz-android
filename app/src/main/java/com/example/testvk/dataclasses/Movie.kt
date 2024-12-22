package com.example.testvk.dataclasses

data class Movie(
    val id: Int,
    val name: String,
    val alternativeName: String,
    val year: String,
    val rating: Rating,
    val movieLength: Int,
    val seriesLength: String,
    val type: String,
    val logo: Logo,
    val genres: List<Genres>,
    val countries: List<Countries>,
    val poster: Poster,
    val description: String? = null,
    val shortDescription: String? = null,
    val persons: List<Person>,
    val releaseYears: List<Years>,
    val seasonsInfo: List<Season>? = null,
    val ageRating: String? = null
)

data class Rating(
    val kp: Double,
    val imdb: Double,
    val filmCritics: Double,
    val russianFilmCritics: Double
)

data class Season(
    val number: Int,
    val episodesCount: Int
)

data class Years(
    val start: String,
    val end: String? = null
)

data class Genres(
    val name: String
)

data class Countries(
    val name: String
)

data class Person(
    val id: Int,
    val photo: String,
    val name: String,
    val description: String,
    val enProfession: String
)

data class Logo(
    val url: String
)

data class Poster(
    val url: String? = null
)

data class Genre(
    val name: String,
    val slug: String
)
