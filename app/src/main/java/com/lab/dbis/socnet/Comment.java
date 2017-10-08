package com.lab.dbis.socnet;

/**
 * Created by harshdepal on 8/10/17.
 */

public class Comment {
    private String name;
    private String content;
    private String timestamp;
    public Comment() {

    }
    public Comment(String name, String timestamp, String content) {
        this.name = name;
        this.content = content;
        this.timestamp = timestamp;
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
