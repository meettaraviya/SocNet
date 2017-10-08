package com.lab.dbis.socnet;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPostFragment extends Fragment {

    private List<Post> postList;
    private String SessionID;
    private ExpandableListView postListView;
    private PostListAdapter postListAdapter;
    private FindPostTask findPostTask;
    public ViewPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        SessionID = getArguments().getString("SessionID");
        postListView = (ExpandableListView) view.findViewById(R.id.expandable_post_list);
        postList = new ArrayList<>();
        postListAdapter = new PostListAdapter(postList,getContext(),SessionID);
        postListView.setAdapter(postListAdapter);
        postListView.setGroupIndicator(null);
        findPostTask = new FindPostTask(SessionID,"0","1000");
        findPostTask.execute((Void) null);
        return view;
    }
    private class FindPostTask extends AsyncTask<Void, Void, Boolean> {
        private final String SessionID;
        private final String offset;
        private final String limit;
        private JSONObject response;

        FindPostTask(String SessionID, String offset, String limit) {
            this.SessionID = SessionID;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("offset",offset);
            paramsMap.put("limit",limit);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.setSessionID(SessionID);
            response = requestHandler.handle(getString(R.string.base_url)+"SeePosts", "POST", paramsMap);
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
                try {
                    JSONArray posts = response.getJSONArray("data");
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        String uid = post.getString("uid");
                        String name = post.getString("name");
                        String postid = post.getString("postid");
                        String content = post.getString("text");
                        String timestamp = post.getString("timestamp");
                        List<Comment> commentList = new ArrayList<>();
                        JSONArray comments = post.getJSONArray("Comment");
                        for (int j=0; j<comments.length(); j++) {
                            JSONObject comment = comments.getJSONObject(j);
                            String cuid = comment.getString("uid");
                            String cname = comment.getString("name");
                            String ccontent = comment.getString("text");
                            String ctimestamp = comment.getString("timestamp");
                            commentList.add(new Comment(cname, ctimestamp, ccontent));
                        }
                        postList.add(new Post(postid,name,timestamp,content,commentList));
                    }
                    postListAdapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            findPostTask = null;
            super.onCancelled();
        }

    }
}
