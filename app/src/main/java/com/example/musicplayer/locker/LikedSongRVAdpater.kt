package com.example.musicplayer.locker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Song.Song
import com.example.musicplayer.databinding.ItemSongBinding

class LikedSongRVAdpater ():
    RecyclerView.Adapter<LikedSongRVAdpater.ViewHolder>() {
    private val songs = ArrayList<Song> ()

    interface DeleteItemClickListener {
        fun onRemoveItem(songId: Int)
    }

    private lateinit var delItemClickListener: DeleteItemClickListener
    fun setMyItemClickListener(itemClickListener: DeleteItemClickListener) {
        delItemClickListener = itemClickListener
    }
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): LikedSongRVAdpater.ViewHolder {
        val binding: ItemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LikedSongRVAdpater.ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.binding.itemSongMoreIv.setOnClickListener {
            delItemClickListener.onRemoveItem(songs[position].id)
            removeItem(position)
        }
    }

    override fun getItemCount(): Int = songs.size



    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSongBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv.text = song.singer
            binding.itemSongImgIv.setImageResource(song.coverImg!!)
        }
    }
}