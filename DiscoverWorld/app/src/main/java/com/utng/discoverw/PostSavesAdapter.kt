package com.utng.discoverw

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.container_post_save.view.*

class PostSavesAdapter(
        private val mContext: Context,
        private val listTopics: List<Post>
) : ArrayAdapter<Post>(mContext, 0, listTopics) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.container_post_save, parent, false)

        val topic = listTopics[position]

        layout.postTitle.text = topic.title
        Picasso.with(mContext)
                .load(topic.image)
                .into(layout.postImage)

        return layout
    }
}
