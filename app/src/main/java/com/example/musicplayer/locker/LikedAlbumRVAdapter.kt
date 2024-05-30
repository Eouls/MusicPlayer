package com.example.musicplayer.locker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Album.Album
import com.example.musicplayer.databinding.ItemLikedAlbumBinding

class LikedAlbumRVAdapter():
    RecyclerView.Adapter<LikedAlbumRVAdapter.ViewHolder>() {
    private val albums = ArrayList<Album> ()
//
//    interface DeleteItemClickListener {
//        fun onRemoveItem(songId: Int)
//    }
//
//    private lateinit var delItemClickListener: DeleteItemClickListener
//    fun setMyItemClickListener(itemClickListener: DeleteItemClickListener) {
//        delItemClickListener = itemClickListener
//    }
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): LikedAlbumRVAdapter.ViewHolder {
        val binding: ItemLikedAlbumBinding = ItemLikedAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LikedAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size



    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int){
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLikedAlbumBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)
        }
    }
}