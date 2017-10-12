package com.lab.dbis.socnet;


import android.app.WallpaperInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private AutoCompleteTextView searchTextBox;
    private Button followButton;
    private Button viewPostButton;
    private Button cancelButton;
    private SearchUserTask searchUserTask;
    private ArrayAdapter<String> adapter;
    private FollowUserTask followUserTask;
    private String SessionID;
    private String uid;
    private HashMap<String, String> uidMap;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        SessionID = getArguments().getString("SessionID");
        uidMap = new HashMap<>();
        uid = null;
        searchUserTask = null;
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line);
        searchTextBox = (AutoCompleteTextView) view.findViewById(R.id.text_search);
        final ImageButton searchButton = (ImageButton) view.findViewById(R.id.button_search);
//        followButton = (Button) view.findViewById(R.id.button_follow);
//        viewPostButton = (Button) view.findViewById(R.id.button_viewpost_search);
//        cancelButton = (Button) view.findViewById(R.id.button_cancel_search);
//        followButton.setVisibility(View.GONE);
//        viewPostButton.setVisibility(View.GONE);
//        cancelButton.setVisibility(View.GONE);
        searchTextBox.setThreshold(3);
        searchTextBox.setAdapter(adapter);
        searchTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = searchTextBox.getText().toString();
                if (input.length() < 3)
                    return;
                if (searchUserTask != null)
                    searchUserTask.cancel(true);
                searchUserTask = new SearchUserTask(SessionID, input);
                searchUserTask.execute((Void) null);
//                followButton.setVisibility(View.GONE);
//                viewPostButton.setVisibility(View.GONE);
//                cancelButton.setVisibility(View.GONE);
                Log.i("Text Change",input);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String input = searchTextBox.getText().toString();
                uid = uidMap.get(input);
                if (uid != null) {

//                    followButton.setVisibility(View.VISIBLE);
//                    viewPostButton.setVisibility(View.VISIBLE);
//                    cancelButton.setVisibility(View.VISIBLE);

                    searchTextBox.dismissDropDown();

                    String[] options = {"Follow","See Posts","Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (which == 0) {
                                followUserTask = new FollowUserTask(SessionID, uid);
                                followUserTask.execute((Void) null);
                                dialogInterface.cancel();
                            }
                            else if (which == 1) {
                                dialogInterface.cancel();
                                Bundle bundle = new Bundle();
                                bundle.putString("SessionID",SessionID);
                                bundle.putString("location", "SeeUserPosts");
                                bundle.putString("uid",uid);
                                ViewPostFragment newFragment = new ViewPostFragment();
                                newFragment.setArguments(bundle);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_placeholder, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                            else {
                                dialogInterface.cancel();
                            }
                        }
                    });
//                    builder.setNeutralButton("Follow",
//                            new DialogInterface.OnClickListener()
//                            {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    followUserTask = new FollowUserTask(SessionID, uid);
//                                    followUserTask.execute((Void) null);
//                                    dialog.cancel();
//                                }
//                            });
//
//                    builder.setNegativeButton("Show Posts",
//                            new DialogInterface.OnClickListener()
//                            {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    dialog.cancel();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("SessionID",SessionID);
//                                    bundle.putString("location", "SeeUserPosts");
//                                    bundle.putString("uid",uid);
//                                    ViewPostFragment newFragment = new ViewPostFragment();
//                                    newFragment.setArguments(bundle);
//                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                                    transaction.replace(R.id.fragment_placeholder, newFragment);
//                                    transaction.addToBackStack(null);
//                                    transaction.commit();
//                                }
//                            });
//
//                    builder.setPositiveButton("Cancel",
//                            new DialogInterface.OnClickListener()
//                            {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    dialog.cancel();
//                                }
//                            });
                    AlertDialog dialog = builder.create();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();

                }
            }
        });

        return view;
    }

    private void setAdapter(List<String> userList) {

    }

    private class FollowUserTask extends AsyncTask<Void, Void, Boolean> {
        private final String SessionID;
        private final String uid;

        FollowUserTask(String SessionID, String uid){
            this.SessionID = SessionID;
            this.uid = uid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("uid",uid);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.setSessionID(SessionID);
            JSONObject response = requestHandler.handle(getString(R.string.base_url)+"Follow", "POST", paramsMap);
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
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.i("FollowTask","done");
            }
            else
                Log.i("FollowTask","not done");
        }

        @Override
        protected void onCancelled() {

        }
    }
    private class SearchUserTask extends AsyncTask<Void, Void, Boolean> {
        private final String SessionID;
        private final String input;
        private JSONObject response;

        SearchUserTask(String SessionID, String input) {
            this.SessionID = SessionID;
            this.input = input;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("uid",input);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.setSessionID(SessionID);
            response = requestHandler.handle(getString(R.string.base_url)+"SearchUser", "POST", paramsMap);
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
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        uidMap = new HashMap<String, String>();
                        adapter.clear();
                        try {
                            JSONArray users = response.getJSONArray("data").getJSONArray(0);
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject user = users.getJSONObject(i);
                                String id = user.getString("uid");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                adapter.add(id);
                                adapter.add(name);
                                adapter.add(email);
                                uidMap.put(id,id);
                                uidMap.put(name,id);
                                uidMap.put(email,id);
                            }
                            adapter.notifyDataSetChanged();
                            searchTextBox.showDropDown();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            searchUserTask = null;
        }

        @Override
        protected void onCancelled() {
            searchUserTask = null;
            super.onCancelled();
        }

    }

}
