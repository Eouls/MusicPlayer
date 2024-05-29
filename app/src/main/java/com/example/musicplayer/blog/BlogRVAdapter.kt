package com.example.musicplayer.blog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ItemBlogPostBinding
import com.example.musicplayer.post.Post

class BlogRVAdapter (private var posts: ArrayList<Post>) :
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
    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        posts.removeAt(position)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): BlogRVAdapter.ViewHolder {
        val binding: ItemBlogPostBinding =
            ItemBlogPostBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogRVAdapter.ViewHolder, position: Int) {
        holder.bind(posts[position])
        /*holder.binding.itemPostMoreIv.setOnClickListener {
            mItemClickListener.onRemoveItem(posts[position].id)
            removeItem(position)
        }*/
    }

    // 데이터 세트 크기 함수
    override fun getItemCount(): Int = posts.size

    // 뷰 홀더
    inner class ViewHolder(val binding: ItemBlogPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.itemPostTitleTv.text = post.title
            binding.itemPostUserNameTv.text = post.name
            binding.itemPostContentTv.text = post.content
            binding.itemPostDateTv.text = post.date

            // 이미지 경로를 통해 이미지 설정
            post.imagePath?.let { imagePath ->
                Glide.with(binding.itemPostImgIv.context)
                    .load(imagePath)
                    .into(binding.itemPostImgIv)
            } ?: run {
                binding.itemPostImgIv.setImageResource(R.drawable.img_post_default) // 기본 이미지 설정
            }
        }
    }

    fun setData(newPosts: List<Post>) {
        posts = newPosts as ArrayList<Post>
        notifyDataSetChanged()
    }
}