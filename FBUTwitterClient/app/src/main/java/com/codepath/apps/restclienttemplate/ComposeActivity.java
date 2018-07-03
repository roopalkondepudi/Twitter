package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.tvTweet) EditText tvTweet;
    @BindView(R.id.btnTweet) Button btnTweet;

    TwitterClient client;
    Tweet tweet;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        client = TwitterApp.getRestClient(this);
    }

    public void onSubmit(View v)
    {
        // Toast.makeText(ComposeActivity.this, "Success", Toast.LENGTH_SHORT).show();
        message = tvTweet.getText().toString(); // get the message
        client.sendTweet(message, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                try {
                    //construct a new Tweet using JSON object passed in
                    tweet = Tweet.fromJSON(response);
                    //send back the new tweet as a result back to the original activity
                    Intent intent = new Intent(ComposeActivity.this, TimelineActivity.class);
                    intent.putExtra("myTweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, intent);
                    finish();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ComposeActivity", throwable.getMessage());
            }
        });

    }
}
