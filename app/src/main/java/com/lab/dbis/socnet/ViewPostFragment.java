package com.lab.dbis.socnet;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private ViewPostTask viewPostTask;
    private PostListAdapter postListAdapter;
    private String location;
    private String uid;
    private String SessionID;
    int offset;
    int limit;
    public void toast(final String msg, final int duration){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), msg, duration).show();

            }
        });
    }

    public void updateOffset(int offset) {
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
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        ExpandableListView postListView = (ExpandableListView) view.findViewById(R.id.expandable_post_list);
        offset = 0;
        limit = 10;
        postListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                if (i + i1 == i2) {
                    if (viewPostTask != null)
                        return;
                    viewPostTask = new ViewPostTask(offset, limit);
                    viewPostTask.execute((Void) null);
                }
            }
        });
        postList = new ArrayList<Post>();
        postListAdapter = new PostListAdapter(postList,getContext(), SessionID);
        postListView.setAdapter(postListAdapter);
        postListView.setGroupIndicator(null);

        return view;
    }



    private class ViewPostTask extends AsyncTask<Void, Void, Boolean> {

        private String offset;
        private String limit;
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
                for(int i=0; i<jsonPostArray.length(); i++)
                    postList.add(new Post(jsonPostArray.getJSONObject(i)));
                int postCount = jsonPostArray.length(),previousOffset = Integer.parseInt(offset);
                int newOffset = previousOffset + postCount;
                updateOffset(newOffset);
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
            viewPostTask = null;
        }

        @Override
        protected void onCancelled() {
            viewPostTask = null;
        }
    }
}
