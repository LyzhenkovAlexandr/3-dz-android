package com.example.testvk.moviescreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testvk.R
import com.example.testvk.network.ApiService
import com.example.testvk.network.ApiService.NetworkConfig.networkDelayMillis
import com.example.testvk.network.MovieListViewModelFactory
import com.example.testvk.network.MovieRepository
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso

private const val EXTRA_ID = "EXTRA_ID"
private const val MOVIE = "movie"
private const val TV_SERIES = "tv-series"

class MovieScreenFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MovieListViewModelFactory(
                MovieRepository(ApiService.create(), networkDelayMillis),
                requireArguments().getInt(EXTRA_ID, -1)
            )
        )[MovieScreenViewModel::class.java]
    }

    private lateinit var movieName: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieCountry: TextView
    private lateinit var movieRating: TextView
    private lateinit var movieDuration: TextView
    private lateinit var movieGenre: TextView
    private lateinit var actorsTextView: TextView
    private lateinit var toolBar: MaterialToolbar
    private lateinit var moviePoster: ImageView
    private lateinit var shortDescriptionText: TextView
    private lateinit var fullDescription: TextView
    private lateinit var seasonAndSeries: TextView
    private lateinit var ageRatingText: TextView
    private lateinit var actorslist: RecyclerView
    private lateinit var imageProgress: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_movie_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieName = view.findViewById(R.id.movieName)
        movieYear = view.findViewById(R.id.movieYear)
        movieCountry = view.findViewById(R.id.movieCountry)
        movieRating = view.findViewById(R.id.movieRating)
        toolBar = view.findViewById(R.id.topAppBar)
        moviePoster = view.findViewById(R.id.moviePoster)
        movieGenre = view.findViewById(R.id.movieGenre)
        fullDescription = view.findViewById(R.id.fullDescription)
        seasonAndSeries = view.findViewById(R.id.seasonAndSeries)
        movieDuration = view.findViewById(R.id.movieDuration)
        shortDescriptionText = view.findViewById(R.id.shortDescription)
        ageRatingText = view.findViewById(R.id.ageRating)
        actorslist = view.findViewById(R.id.actorslist)
        actorsTextView = view.findViewById(R.id.actors)
        imageProgress = view.findViewById(R.id.image_progress)
        errorLayout = view.findViewById(R.id.error_layout)
        retryButton = view.findViewById(R.id.retry_button)
        progressBar = view.findViewById(R.id.progress_bar)

        val layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
        val adapterPerson = PersonAdapter()
        actorslist.adapter = adapterPerson
        actorslist.layoutManager = layoutManager

        viewModel.movie.observe(viewLifecycleOwner) { someMovie ->
            someMovie?.let { movie ->

                progressBar.visibility = View.GONE
                errorLayout.visibility = View.GONE

                with(movie) {
                    movieName.text = name
                    movieCountry.text = countries.joinToString { it.name }
                    movieGenre.text = genres.joinToString { it.name }
                    movieRating.text =
                        if (rating.kp == 0.0) getString(R.string.rating_formed) else "KP: %.2f".format(
                            rating.kp
                        )
                    ageRatingText.visibility =
                        if (ageRating.isNullOrEmpty()) View.GONE else View.VISIBLE
                    ageRatingText.text = ageRating.plus("+")
                    shortDescriptionText.visibility =
                        if (shortDescription.isNullOrEmpty()) View.GONE else View.VISIBLE
                    shortDescriptionText.text = shortDescription
                    if (!description.isNullOrEmpty()) {
                        fullDescription.visibility = View.VISIBLE
                        fullDescription.text = getString(R.string.full_description)
                        fullDescription.setOnClickListener {
                            val args = Bundle().apply {
                                putString("shortDESCRIPTION", shortDescription)
                                putString("fullDESCRIPTION", description)
                            }
                            FullDescriptionBottomSheet().apply {
                                arguments = args
                            }.show(parentFragmentManager, "FullDescriptionBottomSheet")
                        }
                    } else {
                        fullDescription.visibility = View.GONE
                    }

                    when (type) {
                        MOVIE -> {
                            movieDuration.visibility =
                                if (movieLength == 0) View.GONE else View.VISIBLE
                            movieDuration.text = if (movieLength == 0) "" else "$movieLength мин"
                            seasonAndSeries.visibility = View.GONE
                            movieYear.text = year
                        }

                        TV_SERIES -> {
                            movieDuration.visibility =
                                if (seriesLength == "0") View.GONE else View.VISIBLE
                            movieDuration.text =
                                if (seriesLength == "0") "" else "$seriesLength мин"
                            if (releaseYears.first().end == null) {
                                movieYear.textSize = 14F
                            }
                            movieYear.text =
                                releaseYears.joinToString(separator = " - ") { "${it.start} - ${it.end ?: "Настоящее время"}" }
                            if (!seasonsInfo.isNullOrEmpty()) {
                                seasonAndSeries.visibility = View.VISIBLE
                                val totalSeasons = seasonsInfo.lastOrNull()?.number ?: 0
                                val totalEpisodes =
                                    seasonsInfo.fold(0) { acc, season -> acc + season.episodesCount }
                                seasonAndSeries.text = buildString {
                                    append(totalSeasons.toSeasonsString())
                                    append("\n")
                                    append(totalEpisodes.toEpisodesString())
                                }
                            }
                        }
                    }

                    toolBar.title = name
                    poster.url?.let {
                        Picasso.get()
                            .load(poster.url)
                            .resize(300, 450)
                            .into(moviePoster, object : com.squareup.picasso.Callback {

                                override fun onSuccess() {
                                    imageProgress.visibility = View.GONE
                                }

                                override fun onError(e: Exception?) {
                                    imageProgress.visibility = View.GONE
                                    moviePoster.setImageResource(R.drawable.stub)
                                }
                            })
                    } ?: moviePoster.setImageResource(R.drawable.stub)

                    val actors = persons.filter { it.enProfession == "actor" }
                    if (actors.isEmpty()) {
                        actorsTextView.text = getString(R.string.no_actor_information)
                    } else {
                        actorsTextView.text = getString(R.string.actors)
                        adapterPerson.submitList(actors)
                    }
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                progressBar.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
            }
        }

        retryButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
            viewModel.reloadMovie(requireArguments().getInt(EXTRA_ID, -1))
        }


        toolBar.setNavigationOnClickListener {
            Log.d(
                "MovieScreenFragment",
                "Back button clicked, popping fragment from back stack."
            )
            parentFragmentManager.popBackStack()
        }

    }

    private fun Int.toSeasonsString(): String =
        when {
            this % 10 == 1 && this % 100 != 11 -> "$this сезон"
            this % 10 in 2..4 && (this % 100 < 10 || this % 100 >= 20) -> "$this сезона"
            else -> "$this сезонов"
        }

    private fun Int.toEpisodesString(): String =
        when {
            this % 10 == 1 && this % 100 != 11 -> "$this серия"
            this % 10 in 2..4 && (this % 100 < 10 || this % 100 >= 20) -> "$this серии"
            else -> "$this серий"
        }
}
