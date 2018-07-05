package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ReplyActivity extends AppCompatActivity {

    @BindView(R.id.ivReplyingProfileImage) ImageView ivReplyingProfileImage;
    @BindView(R.id.ivReply) ImageView ivReply;
    @BindView(R.id.ivBackButton) ImageView ivBackButton;
    @BindView(R.id.tweetButton) Button replyButton;
    @BindView(R.id.tvReplyingUsername) TextView tvUsername;
    @BindView(R.id.tvReplyingScreenname) TextView tvScreenname;
    @BindView(R.id.inReplyTo) TextView inReplyTo;
    @BindView(R.id.etReply) EditText etReply;

    String message;
    Tweet tweet;
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        ButterKnife.bind(this);

        client = TwitterApp.getRestClient(this);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        //get the name of the person who is being replied to
        String repliedTo = tweet.user.name;
        inReplyTo.setText("In reply to " + repliedTo);
    }

    public void onSubmit(View v)
    {
        message = tweet.getUser().screenName + " " + etReply.getText().toString(); // get the message
        long uid = tweet.getUid();
        client.replyToTweet(message, uid, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                finish();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ReplyActivity", "failed to reply to tweet");
            }
        });

    }
}
