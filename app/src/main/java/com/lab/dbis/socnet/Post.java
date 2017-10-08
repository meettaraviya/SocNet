package com.lab.dbis.socnet;

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
    public Post(String id, String name, String timestamp, String content, List<Comment> commentList) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.content = content;
        this.commentList = commentList;
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
