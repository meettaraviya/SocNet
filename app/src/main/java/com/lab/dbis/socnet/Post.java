package com.lab.dbis.socnet;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshdepal on 8/10/17.
 */

public class Post {
    private String id;
    private String name;
    private String timestamp;
    private String content;
    private List<Comment> commentList;
    public Post() {

    }
    public Post(JSONObject postObj) throws JSONException {
        id = postObj.getString("uid");
        name = postObj.getString("name");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            long time = sdf.parse(postObj.getString("timestamp")).getTime();
            long now = System.currentTimeMillis();
            timestamp = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            timestamp = postObj.getString("timestamp");
        }

        content = postObj.getString("text");
        JSONArray jsonCommentArray = postObj.getJSONArray("Comment");
        commentList = new ArrayList<Comment>();
        for(int i=0; i<jsonCommentArray.length();i++)
            commentList.add(new Comment(jsonCommentArray.getJSONObject(i)));
    }
    public int commentListSize() {
        return commentList.size();
    }
    public Comment getComment(int i) {
        return commentList.get(i);
    }
    public String getName() {
        return name;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getContent() {
        return  content;
    }


}
