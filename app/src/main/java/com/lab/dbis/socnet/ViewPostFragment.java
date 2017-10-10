package com.lab.dbis.socnet;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPostFragment extends Fragment {

    private List<Post> postList;
    private List<Boolean> commentList;
    private ViewPostTask viewPostTask;
    private PostListAdapter postListAdapter;
    private ExpandableListView postListView;
    private String location;
    private String uid;
    private String SessionID;
    private int offset;
    private int limit;
    private boolean reachedTop;
    public void toast(final String msg, final int duration){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), msg, duration).show();

            }
        });
    }

    public void updateOffset(int offset) {
        if (this.offset == 0 && offset > 0)
            postListView.setSelectedGroup(offset-1);
        if (this.offset == offset)
            reachedTop = true;
        this.offset = offset;
    }


    public ViewPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle bundle){
        uid = bundle.getString("uid");
        location = bundle.getString("location");
        SessionID = bundle.getString("SessionID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        offset = 0;
        limit = 10;
        reachedTop = false;
        final View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        postListView = (ExpandableListView) view.findViewById(R.id.expandable_post_list);

        postListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItem) {

                if (firstVisibleItem == 0) {
                    if (viewPostTask == null && !reachedTop) {
                        viewPostTask = new ViewPostTask(offset,limit);
                        viewPostTask.execute((Void) null);
                    }
                }
            }
        });



        postList = new ArrayList<Post>();
        commentList = new ArrayList<Boolean>();
        postListAdapter = new PostListAdapter(postList,commentList,getContext(), SessionID);
        postListView.setAdapter(postListAdapter);
        postListView.setGroupIndicator(null);
        postListView.setStackFromBottom(true);
        return view;
    }



    private class ViewPostTask extends AsyncTask<Void, Void, Boolean> {

        private String offset;
        private String limit;
        private int newOffset;
        ViewPostTask(int offset,int limit) {
            this.offset = Integer.toString(offset);
            this.limit = Integer.toString(limit);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String,String> paramsMap = new HashMap<>();
            if(uid !=null)
                paramsMap.put("uid", uid);
            paramsMap.put("offset",offset);
            paramsMap.put("limit",limit);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.doStoreCookie(true);
            requestHandler.setSessionID(SessionID);
            Log.i("SessionID", SessionID);
            JSONObject response = requestHandler.handle(getString(R.string.base_url)+location,"POST", paramsMap);
            try {

                JSONArray jsonPostArray = response.getJSONArray("data");
                for(int i=0; i<jsonPostArray.length(); i++) {
                    postList.add(new Post(jsonPostArray.getJSONObject(i)));
                    commentList.add(false);
                }
                int postCount = jsonPostArray.length();
                int previousOffset = Integer.parseInt(offset);
                newOffset = previousOffset + postCount;
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
                ViewPostFragment.this.toast("Server error", Toast.LENGTH_SHORT);
            } catch (NullPointerException e){
                e.printStackTrace();
                ViewPostFragment.this.toast("Not connected to internet", Toast.LENGTH_SHORT);
            }
            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            postListAdapter.notifyDataSetChanged();
            updateOffset(newOffset);
            viewPostTask = null;
        }

        @Override
        protected void onCancelled() {
            viewPostTask = null;
        }
    }
}
