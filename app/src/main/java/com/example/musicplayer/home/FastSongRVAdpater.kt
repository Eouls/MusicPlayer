package com.example.musicplayer.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Song.Song
import com.example.musicplayer.databinding.ItemFastsongBinding

class FastSongRVAdpater(private val songs: ArrayList<Song>):
    RecyclerView.Adapter<FastSongRVAdpater.ViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): FastSongRVAdpater.ViewHolder {
        val binding: ItemFastsongBinding = ItemFastsongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: FastSongRVAdpater.ViewHolder, position: Int) {
        holder.bind(songs[position])
    }



    inner class ViewHolder(val binding: ItemFastsongBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv.text = song.singer
            binding.itemSongImgIv.setImageResource(song.coverImg!!)
        }
    }
}