package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;


    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;
    Tweet composedTweet;

    //request helps identify from which intent you came back
    private final int REQUEST_CODE = 20;


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

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    public void fetchTimelineAsync(int page) {
       tweets.clear(); //delete all the tweets currently in the list
       populateTimeline(); //repopulate the timeline to reload the tweets
       tweetAdapter.addAll(tweets); // add the tweets back into the timeline
       tweetAdapter.notifyDataSetChanged(); // tell the log that something has changed
       swipeContainer.setRefreshing(false); //stop refreshing
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
                startActivityForResult(intent, REQUEST_CODE);
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

    /* OnActivityResult to put the tweet inside the ArrayList once the tweet is send back */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE)
        {
            //get tweet
            composedTweet = Parcels.unwrap(data.getParcelableExtra("myTweet"));
            //put it into the ArrayList
            tweets.add(0, composedTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }
}