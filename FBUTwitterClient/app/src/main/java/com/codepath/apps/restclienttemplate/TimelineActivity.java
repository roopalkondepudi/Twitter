package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.github.scribejava.apis.TwitterApi;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    @BindView(R.id.rvTweet) RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        client = TwitterApp.getRestClient(this);
        populateTimeline();

        //find RecyclerView - done with ButterKnife
        //initialize arrayList (data source)
        tweets = new ArrayList<>();
        //construct adapter
        tweetAdapter = new TweetAdapter(tweets);

        //set up the RecyclerView
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        //set the adapter
        rvTweets.setAdapter(tweetAdapter);
    }

    /*
        Adding an Action Item to the ActionBar
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

    //on options item selected

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch(menuItem.getItemId()) //handles all the menu options
        {
            case R.id.tweetCompose:
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString()); //logging what's happening
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
                //for each entry, deserialize each JSON entry
                for (int i = 0; i < response.length(); i++) {
                    //Tweet tweet = null;
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();

            }
        });

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
}