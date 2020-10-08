package com.utng.discoverw;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostItem> postItems;

    public PostAdapter(List<PostItem> postItems) {
        this.postItems = postItems;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_post, parent, false
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

    static  class PostViewHolder extends RecyclerView.ViewHolder {
        VideoView postView;
        TextView textPostTitle, textPostDescription;
        ProgressBar postProgessBar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postView = itemView.findViewById(R.id.postView);
            textPostTitle = itemView.findViewById(R.id.textPostTitle);
            textPostDescription = itemView.findViewById(R.id.textPostDescription);
            postProgessBar = itemView.findViewById(R.id.postProgressBar);
        }

        void setPostData(PostItem postItem){
            textPostTitle.setText(postItem.postTitle);
            textPostDescription.setText(postItem.postDescription);
            postView.setVideoPath(postItem.postURL);
            postView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    postProgessBar.setVisibility(View.GONE);
                    mp.start();

                    float postRatio = mp.getVideoWidth()/ (float) mp.getVideoHeight();
                    float screenRatio = postView.getWidth()/ (float) postView.getHeight();
                    float scale = postRatio / screenRatio;

                    if (scale >= 1f) {
                        postView.setScaleX(scale);
                    } else {
                        postView.setScaleY(1f/scale);
                    }
                }
            });

            //
            postView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
        }
    }
}
