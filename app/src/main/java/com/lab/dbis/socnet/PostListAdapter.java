package com.lab.dbis.socnet;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harshdepal on 8/10/17.
 */

public class PostListAdapter extends BaseExpandableListAdapter {
    private List<Post> postList;
    private Context context;
    public PostListAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
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
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }
        Post post = postList.get(groupPosition);
        TextView postUserName = (TextView) convertView.findViewById(R.id.text_post_name);
        TextView postTimestamp = (TextView) convertView.findViewById(R.id.text_post_timestamp);
        TextView postContent = (TextView) convertView.findViewById(R.id.text_post_content);
        Button buttonViewComments = (Button) convertView.findViewById(R.id.button_post_view_comments);
        Button buttonAddComment = (Button) convertView.findViewById(R.id.button_post_add_comment);

        postUserName.setText(post.getName());
        postTimestamp.setText(post.getTimestamp());
        postContent.setText(post.getContent());
        buttonViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded)
                    ((ExpandableListView) parent).collapseGroup(groupPosition);
                else
                    ((ExpandableListView) parent).expandGroup(groupPosition,false);
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
        return postList.size();
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return postList.get(groupPosition).commentListSize();
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
}
