package com.lab.dbis.socnet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public Comment(JSONObject postObj) throws JSONException {
        id = postObj.getString("uid");
        name = postObj.getString("name");
        timestamp = postObj.getString("timestamp");
        content = postObj.getString("text");
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
