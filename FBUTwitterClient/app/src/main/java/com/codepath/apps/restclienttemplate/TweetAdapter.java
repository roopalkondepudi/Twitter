package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roopalk on 7/2/18.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    //pass in the Tweets array in the constructor
    Context context;

    private List<Tweet> tweets_;
    public TweetAdapter(List<Tweet> tweets)
    {
        tweets_ = tweets;
    }

    //for each row, inflate layout and cache references into ViewHolder
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    //bind the values based on position of the element
    @Override
    public void onBindViewHolder(TweetAdapter.ViewHolder holder, int position)
    {
        //get data according to position
        Tweet tweet = tweets_.get(position);

        //populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);

        Glide.with(context)
        .load(tweet.user.profileImageURL)
        .into(holder.ivProfileImage);

    }

    //get the number of items
    @Override
    public int getItemCount()
    {
        return tweets_.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        //using ButterKnife to reduce boilerplate
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvBody) TextView tvBody;

        //constructor for the ViewHolder

        public ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);

            //no need to perform findViewById lookups because ButterKnife has bound them already

        }

    }
}
