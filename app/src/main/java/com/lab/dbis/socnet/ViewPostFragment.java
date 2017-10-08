package com.lab.dbis.socnet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPostFragment extends Fragment {

    private List<Post> postList;
    public ViewPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        ExpandableListView postListView = (ExpandableListView) view.findViewById(R.id.expandable_post_list);
        populatePostList();
        PostListAdapter postListAdapter = new PostListAdapter(postList,getContext());
        postListView.setAdapter(postListAdapter);
        postListView.setGroupIndicator(null);

        return view;
    }
    private void populatePostList() {
        List<Comment> commentList = new ArrayList<>();
        postList = new ArrayList<>();
        Comment comment = new Comment("1","Depal","02-08-1997","Welcome!");
        commentList.add(comment);
        Post post = new Post("1","Harsh","01-08-1997","Hello World!", commentList);
        postList.add(post);
        post = new Post("2","Depal","08-01-2017","Testing...", commentList);
        postList.add(post);
    }

}
