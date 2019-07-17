package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseFile;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    Context context;
    ArrayList<Share> shares;
    public ProfileAdapter(ArrayList<Share> shares) {
        this.shares = shares;
    }
    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(postView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Share share = shares.get(position);
        ParseFile img = share.getImage();
        String imgUrl = "";
        if (img != null) {
            imgUrl = img.getUrl();
        }
        Glide.with(context)
                .load(imgUrl)
                .into(holder.profileImage);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profilePic);

        }
    }
}
