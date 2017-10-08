package com.lab.dbis.socnet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        timestamp = postObj.getString("timestamp");
        content = postObj.getString("text");
        JSONArray jsonCommentArray = postObj.getJSONArray("Comment");
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
