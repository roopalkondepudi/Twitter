package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by roopalk on 7/2/18.
 */

@Parcel
public class Tweet
{
    //attributes
    public String body;
    public long uid; //database ID for the tweet
    public String createdAt;
    public User user;
    public boolean favorited;

    public Tweet()
    {

    }

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {

        Tweet tweet = new Tweet();
        //extract values
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.favorited = jsonObject.getBoolean("favorited");
        return tweet;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return getTimeStamp(createdAt);
    }

    public User getUser() {
        return user;
    }

    public boolean getFavorite()
    {
        return favorited;
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
