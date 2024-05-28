package com.example.musicplayer.Blog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Profile.Post
import com.example.musicplayer.databinding.ItemPostBinding

class BlogRVAdapter (private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<BlogRVAdapter.ViewHolder>() {

    //클릭 인터페이스 정의
    interface MyItemClickListener {
        fun onRemoveItem(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }


    // 저장된 글 삭제
    fun removeItem(position: Int) {
        postList.removeAt(position)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): BlogRVAdapter.ViewHolder {
        val binding: ItemPostBinding =
            ItemPostBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogRVAdapter.ViewHolder, position: Int) {
        holder.bind(postList[position])
        holder.binding.itemPostMoreIv.setOnClickListener {
            mItemClickListener.onRemoveItem(position)
            removeItem(position)
        }
    }

    // 데이터 세트 크기 함수
    override fun getItemCount(): Int = postList.size

    // 뷰 홀더
    inner class ViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.itemPostImgIv.setImageResource(post.postImg!!)
            binding.itemPostTitleTv.text = post.title
            binding.itemPostUserNameTv.text = post.name
            binding.itemPostContentTv.text = post.content
            binding.itemPostDateTv.text = post.date
        }
    }
}