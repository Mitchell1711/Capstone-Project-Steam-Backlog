package com.example.steambacklog.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.steambacklog.R
import com.example.steambacklog.model.Games
import kotlinx.android.synthetic.main.item_game.view.*
import kotlin.reflect.KFunction0

class GameAdapter(private val games: List<Games>, private val onClick: (Games) -> Unit) :
    RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_game, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { onClick(games[adapterPosition]) }
        }

        fun bind(games: Games) {
            Glide.with(context).load(games.getLogoUrl()).into(itemView.ivGame)
        }
    }
}
