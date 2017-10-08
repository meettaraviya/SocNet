package com.lab.dbis.socnet;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private String location;
    private String uid;
    private String SessionID;

    public void toast(final String msg, final int duration){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), msg, duration).show();

            }
        });
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
        postList = new ArrayList<Post>();
        if(viewPostTask!=null)
            viewPostTask.cancel(true);
        viewPostTask = new ViewPostTask();
        viewPostTask.execute();

        PostListAdapter postListAdapter = new PostListAdapter(postList,getContext());
        postListView.setAdapter(postListAdapter);
        postListView.setGroupIndicator(null);

        return view;
    }



    private class ViewPostTask extends AsyncTask<Void, Void, Boolean> {

        ViewPostTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String,String> paramsMap = new HashMap<>();
            if(uid !=null)
                paramsMap.put("uid", uid);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.doStoreCookie(true);
            requestHandler.setSessionID(SessionID);
            Log.i("SessionID", SessionID);
            JSONObject response = requestHandler.handle(getString(R.string.base_url)+location,"POST", paramsMap);
            try {

                JSONArray jsonPostArray = response.getJSONArray("data");
                for(int i=0; i<jsonPostArray.length(); i++)
                    postList.add(new Post(jsonPostArray.getJSONObject(i)));
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
            viewPostTask = null;
        }

        @Override
        protected void onCancelled() {
            viewPostTask = null;
        }
    }
}
