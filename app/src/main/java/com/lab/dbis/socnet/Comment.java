package com.lab.dbis.socnet;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by harshdepal on 8/10/17.
 */

public class Comment {
    private String id;
    private String name;
    private String content;
    private String timestamp;
    public Comment() {

    }
    public Comment(JSONObject commentObj) throws JSONException {
        id = commentObj.getString("uid");
        name = commentObj.getString("name");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            long time = sdf.parse(commentObj.getString("timestamp")).getTime();
            long now = System.currentTimeMillis();
            timestamp = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            timestamp = commentObj.getString("timestamp");
        }
        content = commentObj.getString("text");
    }
    public String getName() {
        return name;
    }
    public String getContent() {
        return content;
    }
    public String getTimestamp() {
        return timestamp;
    }
}
