package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by roopalk on 7/2/18.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    //pass in the Tweets array in the constructor
    Context context;

    List<Tweet> tweets_;

    TwitterClient client;

    public TweetAdapter(List<Tweet> tweets)
    {
        tweets_ = tweets;
        client = TwitterApp.getRestClient(context);
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
        holder.tvHandle.setText(tweet.user.screenName);
        holder.tvTimeStamp.setText(this.getTimeStamp(tweet.createdAt));

        GlideApp.with(context)
                .load(tweet.user.profileImageURL)
                .into(holder.ivProfileImage);

        //checking if there is an image or not

        if(tweet.favorited)
        {
            holder.ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart);
        }
        else
        {
            holder.ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        if(tweet.imageURL == null)
        {
            holder.ivEmbeddedImage.setVisibility(View.GONE);
        }
        else
        {
            holder.ivEmbeddedImage.setVisibility(View.VISIBLE);
            GlideApp.with(context)
                    .load(tweet.imageURL)
                    .into(holder.ivEmbeddedImage);
        }

    }

    //get the number of items
    @Override
    public int getItemCount()
    {
        return tweets_.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements com.codepath.apps.restclienttemplate.ViewHolder, View.OnClickListener {
        //using ButterKnife to reduce boilerplate
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvHandle) TextView tvHandle;
        @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
        @BindView(R.id.ivReplyIcon) ImageView ivReplyIcon;
        @BindView(R.id.ivFavoriteIcon) ImageView ivFavoriteIcon;
        @BindView(R.id.ivEmbeddedImage) ImageView ivEmbeddedImage;
        //constructor for the ViewHolder

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            view.setOnClickListener(this);
            ivReplyIcon.setOnClickListener(this);
            ivFavoriteIcon.setOnClickListener(this);

            //no need to perform findViewById lookups because ButterKnife has bound them already
        }

        //onClick to go to the Tweet details
        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();
            //Toast.makeText(context,"click", Toast.LENGTH_LONG).show();
            //check if the position is valid
            if(position != RecyclerView.NO_POSITION)
            {
                //get tweet at that position
                Tweet tweet = tweets_.get(position);

                if(v.getId() == R.id.ivReplyIcon)
                {
                    Intent intent = new Intent(context, ReplyActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);
                }
                else if(v.getId() == R.id.ivFavoriteIcon)
                {
                    if(tweet.getFavorite())
                    {
                        tweet.favorited = false;
                        //Toast.makeText(getBaseContext(), "unfavorited", Toast.LENGTH_LONG).show();
                        ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart_stroke);
                        client.unFavorite(tweet.getUid(), new JsonHttpResponseHandler()
                        {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("HomeTimeLine", "unfavorited!");
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                Log.e("HomeTimeLine", throwable.getMessage());
                            }
                        });
                    }
                    else {
                        tweet.favorited = true;
                        //Toast.makeText(getBaseContext(), "favorited", Toast.LENGTH_LONG).show();
                        ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart);
                        client.favoriteTweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.i("TweetDetailsActivity", "favorited!");
                            }
                        });
                    }
                }
                else
                {
                    //create intent for new activity
                    Intent intent = new Intent(context, TweetDetailsActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));

                    //start the new activity
                    context.startActivity(intent);
                }
            }
        }
    }

    //add the timestamp to every tweet displayed
    public String getTimeStamp(String dateInJSON)
    {
        String twitterDateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        /*SimpleDateFormat - a class for formatting and parsing dates in a locale-sensitive manner
         * EEE - day in week (letter I'm guessing, e.g. MON)
         * MMM - month in year (also letter, e.g. MAY)
         * dd/HH/mm/ss pretty straightforward time stuff
         * ZZZZZ - time zone, although I'm not sure why there are 5
         * yyyy - year
         */
        SimpleDateFormat time = new SimpleDateFormat(twitterDateFormat, Locale.ENGLISH);
        /* Locale - object that represents a specific geographical/political/cultural region
         * Locale(String language, String country) OR Locale(String country), the latter of which we are using in this context
         * I could easily say English, US
         * Country and Language is used with ISO codes
         */

        /*setLenient the time parsing might be lenient*/
        time.setLenient(true);

        String timeStamp = "";
        try
        {
            long dateInMilliseconds = time.parse(dateInJSON).getTime();
            timeStamp = DateUtils.getRelativeTimeSpanString(dateInMilliseconds, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return timeStamp;
    }

    /* Within the RecyclerView.Adapter class */
    // Clean all elements of the recycler
    public void clear() {
        tweets_.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets_.addAll(list);
        notifyDataSetChanged();
    }
}
