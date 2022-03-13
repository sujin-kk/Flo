package com.example.flo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.Class.Album
import com.example.flo.databinding.ItemSavedAlbumBinding

class LockerSavedAlbumRVAdapter() : RecyclerView.Adapter<LockerSavedAlbumRVAdapter.ViewHolder>() {

    private val albums = ArrayList<Album>()

    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: ItemSavedAlbumBinding = ItemSavedAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albums[position])
        holder.binding.itemSavedAlbumMoreIv.setOnClickListener {
            removeSong(position)
            if(albums.size!=0)
                mItemClickListener.onRemoveSong(albums[position].id)
        }
    }

    override fun getItemCount(): Int = albums.size

    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()
    }

    fun removeSong(position: Int) {
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSavedAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemSavedAlbumTitleTv.text = album.title
            binding.itemSavedAlbumSingerTv.text = album.singer
            binding.itemSavedAlbumImgIv.setImageResource(album.coverImg!!)
        }
    }

}