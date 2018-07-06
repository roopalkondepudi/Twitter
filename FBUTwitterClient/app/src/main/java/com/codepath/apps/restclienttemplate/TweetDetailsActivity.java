package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailsActivity extends AppCompatActivity {

    //ButterKnife to bind the variables
    @BindView(R.id.tvHandle)
    TextView tvHandle;
    @BindView(R.id.tvTweetBody)
    TextView tvTweetBody;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvTimeStamp)
    TextView tvTimeStamp;
    @BindView(R.id.ivprofileImage)
    ImageView ivprofileImage;
    @BindView(R.id.ivReplyIcon)
    ImageView ivReplyIcon;
    @BindView(R.id.ivFavoriteIcon)
    ImageView ivFavoriteIcon;

    //the tweet that was clicked on
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        //unwrapping the tweet
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        //setting the handle, body, and username
        tvHandle.setText(tweet.getUser().getScreenName());
        tvTweetBody.setText(tweet.getBody());
        tvUserName.setText(tweet.getUser().getName());
        tvTimeStamp.setText(tweet.getCreatedAt());

        Log.d("time stamp", tvTimeStamp.toString());


        ivFavoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(tweet.getFavorite())
                {
                    tweet.favorited = false;
                    //Toast.makeText(getBaseContext(), "unfavorited", Toast.LENGTH_LONG).show();
                    ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart_stroke);
                }
                else
                {
                    tweet.favorited = true;
                    //Toast.makeText(getBaseContext(), "favorited", Toast.LENGTH_LONG).show();
                    ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart);
                }
            }
        });
        //setting the image with Glide
        GlideApp.with(this)
                .load(tweet.getUser().getProfileImageURL())
                .into(ivprofileImage);
    }
}