package com.utng.discoverw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postItems;

    public PostAdapter(List<Post> postItems) {
        this.postItems = postItems;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.container_post, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.setPostData(postItems.get(position));
    }

    @Override
    public int getItemCount() {
        return postItems.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView postView;
        private TextView textPostTitle, textPostDescription;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postView = itemView.findViewById(R.id.postView);
            textPostTitle = itemView.findViewById(R.id.textPostTitle);
            textPostDescription = itemView.findViewById(R.id.textPostDescription);
        }

        void setPostData(Post postItem) {
            textPostTitle.setText(postItem.getTitle());
            textPostDescription.setText(postItem.getDescription());
            Picasso.with(itemView.getContext())
                    .load(postItem.getImage())
                    .error(R.drawable.ic_error)
                    .into(postView);
        }
    }
}
