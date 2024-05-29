package com.example.musicplayer.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ItemProfilePostBinding
import com.example.musicplayer.post.Post

class ProfileRVAdapter (private var posts: ArrayList<Post>) :
    RecyclerView.Adapter<ProfileRVAdapter.ViewHolder>() {

    //클릭 인터페이스 정의
    interface MyItemClickListener {
        fun onRemoveItem(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    // 특정 위치의 post 반환
    fun getPost(position: Int): Post {
        return posts[position]
    }

    // 저장된 글 삭제
    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        posts.removeAt(position)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ProfileRVAdapter.ViewHolder {
        val binding: ItemProfilePostBinding =
            ItemProfilePostBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileRVAdapter.ViewHolder, position: Int) {
        holder.bind(posts[position])
        holder.binding.itemMypostMoreIv.setOnClickListener {
            mItemClickListener.onRemoveItem(posts[position].id)
            removeItem(position)
        }
    }

    // 데이터 세트 크기 함수
    override fun getItemCount(): Int = posts.size

    // 뷰 홀더
    inner class ViewHolder(val binding: ItemProfilePostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.itemMypostTitleTv.text = post.title
            binding.itemMypostContentTv.text = post.content
            binding.itemMypostDateTv.text = post.date

            // 이미지 경로를 통해 이미지 설정
            post.imagePath?.let { imagePath ->
                Glide.with(binding.itemMypostImgIv.context)
                    .load(imagePath)
                    .into(binding.itemMypostImgIv)
            } ?: run {
                binding.itemMypostImgIv.setImageResource(R.drawable.img_post_default) // 기본 이미지 설정
            }
        }
    }

    fun setData(newPosts: List<Post>) {
        posts = newPosts as ArrayList<Post>
        notifyDataSetChanged()
    }
}