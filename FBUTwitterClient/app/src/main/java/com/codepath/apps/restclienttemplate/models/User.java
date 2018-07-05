package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by roopalk on 7/2/18.
 */

@Parcel
public class User
{
    //attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageURL;

    //deserialize JSON
    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        //get information from JSON object
        user.name = jsonObject.getString("name"); //username
        user.uid = jsonObject.getLong("id");
        user.screenName = "@" + jsonObject.getString("screen_name"); //handle
        user.profileImageURL = jsonObject.getString("profile_image_url");

        return user;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }
}
