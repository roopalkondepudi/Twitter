package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

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
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        //unwrapping the tweet
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        client = TwitterApp.getRestClient(this);

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
                else
                {
                    tweet.favorited = true;
                    //Toast.makeText(getBaseContext(), "favorited", Toast.LENGTH_LONG).show();
                    ivFavoriteIcon.setImageResource(R.drawable.ic_vector_heart);
                    client.favoriteTweet(tweet.getUid(), new JsonHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("TweetDetailsActivity", "favorited!");
                        }
                    });
                }
            }
        });

        ivReplyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReplyActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                startActivity(intent);

            }
        });
        //setting the image with Glide
        GlideApp.with(this)
                .load(tweet.getUser().getProfileImageURL())
                .into(ivprofileImage);
    }
}