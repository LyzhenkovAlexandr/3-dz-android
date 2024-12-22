package com.example.testvk.movielist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testvk.R
import com.example.testvk.moviescreen.MovieScreenFragment
import com.example.testvk.network.ApiService
import com.example.testvk.network.ApiService.NetworkConfig.networkDelayMillis
import com.example.testvk.network.MovieListViewModelFactory
import com.example.testvk.network.MovieRepository

class MovieListFragment : Fragment() {

    private lateinit var movieRecycler: RecyclerView
    private lateinit var genresList: RecyclerView
    private lateinit var retryButton: Button
    private lateinit var viewModel: MovieListViewModel
    private var isClearingData = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_movie_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieRecycler = view.findViewById(R.id.movieList)
        genresList = view.findViewById(R.id.genresList)
        retryButton = view.findViewById(R.id.retry_button)

        viewModel = ViewModelProvider(
            requireActivity(),
            MovieListViewModelFactory(MovieRepository(ApiService.create(), networkDelayMillis))
        )[MovieListViewModel::class.java]

        val adapter = MovieAdapter { id ->
            context?.let { viewModel.selectMovie(id) }
        }
        val loadStateAdapter = LoadStateAdapter { viewModel.retry() }
        val concatAdapter = adapter.withLoadStateFooter(loadStateAdapter)
        movieRecycler.adapter = concatAdapter
        val layoutManager = LinearLayoutManager(requireContext())

        movieRecycler.layoutManager = layoutManager

        viewModel.movies.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        val adapterGenres = GenreAdapter { genre ->
            context?.let { viewModel.chooseGenre(genre) }
        }
        genresList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        genresList.adapter = adapterGenres

        viewModel.genres.observe(viewLifecycleOwner) { genres ->
            adapterGenres.submitList(genres)
        }

        adapter.addLoadStateListener { loadState ->
            val isLoading = loadState.source.refresh is LoadState.Loading
            val isError = loadState.source.refresh is LoadState.Error
            Log.d("MovieListFragment", "isLoading: $isLoading, isError: $isError")

            view.findViewById<View>(R.id.progress_bar)?.visibility =
                if (isLoading) View.VISIBLE else View.GONE

            view.findViewById<View>(R.id.error_layout)?.visibility =
                if (isError) View.VISIBLE else View.GONE

            movieRecycler.visibility = if (isError) View.GONE else View.VISIBLE
            genresList.visibility = if (isError) View.GONE else View.VISIBLE
        }

        retryButton.setOnClickListener {
            viewModel.retry()
        }
        viewModel.selectedMovieId.observe(viewLifecycleOwner) { movieId ->
            movieId?.let {
                Log.d("FragmentNavigation", "Navigating to MovieScreenFragment with ID: $it")
                navigateToMovieDetails(it)
            }
        }
    }

    private fun navigateToMovieDetails(movieId: Int) {
        Log.d(
            "MovieListFragment",
            "Navigating to MovieScreenFragment with ID: $movieId"
        )

        val args = Bundle().apply {
            putInt("EXTRA_ID", movieId)
        }
        val selectedFilmFragment = MovieScreenFragment().apply {
            arguments = args
        }
        viewModel.resetSelectedMovieId()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, selectedFilmFragment)
            .addToBackStack(null)
            .commit()

        Log.d(
            "MovieListFragment",
            "Fragment replaced successfully."
        )
    }
}
