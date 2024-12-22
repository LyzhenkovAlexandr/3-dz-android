package com.example.testvk.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testvk.R
import com.example.testvk.dataclasses.Movie
import com.squareup.picasso.Picasso

class MovieAdapter(
    private val selectedFilm: (Int) -> Unit
) : PagingDataAdapter<Movie, MovieAdapter.ImageViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.itemView.findViewById<ProgressBar>(R.id.image_progress).visibility = View.VISIBLE
        holder.movieName.text = getItem(position)?.name
        holder.movieYear.text = getItem(position)?.year
        holder.movieCounty.text = getItem(position)?.countries?.joinToString { it.name }
        holder.movieGenre.text = getItem(position)?.genres?.joinToString { it.name }
        holder.movieRating.text = buildString {
            append("KP: ")
            append(getItem(position)?.rating?.kp.toString())
        }

        Picasso.get()
            .load(getItem(position)?.poster?.url)
            .resize(72, 108)
            .placeholder(R.drawable.stub)
            .error(R.drawable.stub)
            .into(holder.moviePoster, object : com.squareup.picasso.Callback {

                override fun onSuccess() {
                    holder.itemView.findViewById<ProgressBar>(R.id.image_progress).visibility =
                        View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.itemView.findViewById<ProgressBar>(R.id.image_progress).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageView>(R.id.moviePoster)
                        .setImageResource(R.drawable.stub)
                }
            })
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieName: TextView = itemView.findViewById(R.id.movieName)
        val movieYear: TextView = itemView.findViewById(R.id.movieYear)
        val movieCounty: TextView = itemView.findViewById(R.id.movieCounty)
        val movieGenre: TextView = itemView.findViewById(R.id.movieGenre)
        val movieRating: TextView = itemView.findViewById(R.id.movieRating)
        val moviePoster: ImageView = itemView.findViewById(R.id.moviePoster)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = getItem(position)
                    movie?.let { selectedFilm(it.id) }
                }
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
