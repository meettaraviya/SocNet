package com.lab.dbis.socnet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by harshdepal on 8/10/17.
 */

public class PostListAdapter extends BaseExpandableListAdapter {
    private List<Post> postList;
    private List<Boolean> commentList;
    private Context context;
    private String SessionID;
    public PostListAdapter(List<Post> postList,List<Boolean> commentList, Context context, String SessionID) {
        this.postList = postList;
        this.context = context;
        this.SessionID = SessionID;
        this.commentList = commentList;
    }
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_comment, parent, false);
        }
        Comment comment = postList.get(groupPosition).getComment(childPosition);
        TextView commentUserName = (TextView) convertView.findViewById(R.id.text_comment_name);
        TextView commentTimestamp = (TextView) convertView.findViewById(R.id.text_comment_timestamp);
        TextView commentContent = (TextView) convertView.findViewById(R.id.text_comment_content);
        commentUserName.setText(comment.getName());
        commentTimestamp.setText(comment.getTimestamp());
        commentContent.setText(comment.getContent());
        return convertView;
    }
    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_post, parent, false);
        }
        if (commentList.get(groupPosition) == null) {
            ((ExpandableListView) parent).expandGroup(groupPosition, false);
            commentList.set(groupPosition, false);
        }
        Post post = postList.get(groupPosition);
        TextView postUserName = (TextView) convertView.findViewById(R.id.text_post_name);
        TextView postTimestamp = (TextView) convertView.findViewById(R.id.text_post_timestamp);
        TextView postContent = (TextView) convertView.findViewById(R.id.text_post_content);
        ImageView postImage = (ImageView) convertView.findViewById(R.id.image_post);

        final EditText editTextComment = (EditText) convertView.findViewById(R.id.edittext_post_new_comment);
        final Button buttonViewComments = (Button) convertView.findViewById(R.id.button_post_view_comments);
        Button buttonAddComment = (Button) convertView.findViewById(R.id.button_post_add_comment);

        postUserName.setText(post.getName());
        postTimestamp.setText(post.getTimestamp());
        postContent.setText(post.getContent());
        if(post.getImage()!=null){
            postImage.setImageBitmap(post.getImage());
            postImage.setVisibility(View.VISIBLE);
        }
        else{
            postImage.setVisibility(View.GONE);
        }
        buttonViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean currentState = commentList.get(groupPosition);
                commentList.set(groupPosition,!currentState);
                notifyDataSetChanged();
                editTextComment.setText("");
            }
        });
        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editTextComment.getText().toString();
                String postid = postList.get(groupPosition).getId();
                if (content.equals(""))
                    return;
                AddCommentTask addCommentTask = new AddCommentTask(SessionID,postid,content);
                addCommentTask.execute((Void) null);
            }
        });

        return convertView;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return postList.get(groupPosition).getComment(childPosition).hashCode();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return postList.get(groupPosition);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return postList.get(groupPosition).getComment(childPosition);
    }
    @Override
    public int getGroupCount() {
        if(postList!=null)
            return postList.size();
        else
            return 0;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        int totalComments =  postList.get(groupPosition).commentListSize();
        if (commentList.get(groupPosition) == null) {
            return Math.min(totalComments,3);
        }
        else if (commentList.get(groupPosition) == false) {
            return Math.min(totalComments,3);
        }
        else {
            return totalComments;
        }
    }
    @Override
    public long getGroupId(int groupPosition) {
        return postList.get(groupPosition).hashCode();
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    private class AddCommentTask extends AsyncTask<Void, Void, Boolean> {
        private final String SessionID;
        private final String postid;
        private final String content;
        private JSONObject response;

        AddCommentTask(String SessionID, String postid, String content) {
            this.SessionID = SessionID;
            this.content = content;
            this.postid = postid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("postid",postid);
            System.out.println("POSTID = " + postid);
            paramsMap.put("content",content);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.setSessionID(SessionID);
            response = requestHandler.handle(context.getString(R.string.base_url)+"NewComment", "POST", paramsMap);
            try {
                return response.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected  void onPostExecute(final Boolean success) {
            if (success) {
                
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }


}
