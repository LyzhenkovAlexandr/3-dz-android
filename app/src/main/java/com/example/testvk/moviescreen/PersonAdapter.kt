package com.example.testvk.moviescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testvk.R
import com.example.testvk.dataclasses.Person
import com.squareup.picasso.Picasso

class PersonAdapter : ListAdapter<Person, PersonAdapter.PersonViewHolder>(PersonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.actor_item, parent, false)
        return PersonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.actorName.text = getItem(position)?.name
        holder.description.text = getItem(position)?.description
        Picasso.get()
            .load(getItem(position).photo)
            .resize(54, 81)
            .placeholder(R.drawable.stub)
            .error(R.drawable.stub)
            .into(holder.actorPhoto, object : com.squareup.picasso.Callback {

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

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val actorPhoto: ImageView = itemView.findViewById(R.id.actorPhoto)
        val actorName: TextView = itemView.findViewById(R.id.actorName)
        val description: TextView = itemView.findViewById(R.id.description)
    }

    class PersonDiffCallback : DiffUtil.ItemCallback<Person>() {

        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean =
            oldItem == newItem
    }
}
