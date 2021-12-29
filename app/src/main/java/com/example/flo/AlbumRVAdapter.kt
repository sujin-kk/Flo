package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemAlbumBinding

class AlbumRVAdapter(private val albumList: ArrayList<Album>) :
    RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    // click interface
    interface MyItemClickListener{
        fun onItemClick(album: Album)
        // fun onRemoveAlbum(position: Int)
    }

    // listener 객체 전달 받는 함수 & 저장하는 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    // 뷰 홀더를 생성해줘야 할 때 호출되는 함수 => 아이템 뷰 객체를 만들어서 뷰 홀더에 넣어줌
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
        val binding: ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup,false)

        return ViewHolder(binding)
    }

    // 아이템 뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])

        holder.binding.itemAlbumCoverIv.setOnClickListener {
            mItemClickListener.onItemClick(albumList[position])
        }

//        holder.binding.itemAlbumTitleTv.setOnClickListener {
//            mItemClickListener.onRemoveAlbum(position)
//        }
    }

    fun addItem(album: Album) {
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    // data set 크기를 알려주는 함수 => 리사이클러뷰의 마지막이 어디인지 알 수 있음
    override fun getItemCount(): Int = albumList.size

    // ViewHolder
    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album : Album) {
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverIv.setImageResource(album.coverImg!!)
        }
    }
}