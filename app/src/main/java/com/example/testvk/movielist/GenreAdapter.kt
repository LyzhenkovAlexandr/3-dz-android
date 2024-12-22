package com.example.testvk.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testvk.R
import com.example.testvk.dataclasses.Genre

class GenreAdapter(
    private val chooseGenre: (Genre) -> Unit
) : ListAdapter<Genre, GenreAdapter.TypeViewHolder>(TypeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.genre_list_item, parent, false)
        return TypeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.genre.text = getItem(position).name
    }

    inner class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val genre: TextView = itemView.findViewById(R.id.type)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val genre = getItem(position)
                    genre?.let { chooseGenre(it) }
                }
            }
        }
    }

    class TypeDiffCallback : DiffUtil.ItemCallback<Genre>() {

        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
            oldItem.name == newItem.name
    }
}
