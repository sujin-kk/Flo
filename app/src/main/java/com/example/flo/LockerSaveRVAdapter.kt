package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSaveBinding

class LockerSaveRVAdapter() :
    RecyclerView.Adapter<LockerSaveRVAdapter.ViewHolder>() {
    private val songs = ArrayList<Song>()

    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }

    // listener 객체 전달 받는 함수 & 저장하는 변수
    private lateinit var mItemClickListener: LockerSaveRVAdapter.MyItemClickListener

    fun setMyItemClickListener(itemClickListener: LockerSaveRVAdapter.MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LockerSaveRVAdapter.ViewHolder {
        val binding: ItemSaveBinding = ItemSaveBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerSaveRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])

        holder.binding.itemSaveMoreIb.setOnClickListener {
            removeSong(position)
            mItemClickListener.onRemoveSong(songs[position].id)
        }
    }

    // data set 크기를 알려주는 함수 => 리사이클러뷰의 마지막이 어디인지 알 수 있음
    override fun getItemCount(): Int = songs.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    fun removeSong(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSaveBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.itemSaveTitleTv.text = song.title
            binding.itemSaveSingerTv.text = song.singer
            binding.itemSaveIv.setImageResource(song.coverImg!!)
        }
    }
}