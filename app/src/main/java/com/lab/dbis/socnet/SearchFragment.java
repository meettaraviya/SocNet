package com.lab.dbis.socnet;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private AutoCompleteTextView searchTextBox;
    private SearchUserTask searchUserTask;
    private String SessionID;
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

        searchUserTask = null;

        searchTextBox = (AutoCompleteTextView) view.findViewById(R.id.text_search);
        searchTextBox.setThreshold(3);

        final ImageButton searchButton = (ImageButton) view.findViewById(R.id.button_search);

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
                Log.i("Text Change",input);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private void setAdapter(List<String> userList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, userList);
        searchTextBox.setAdapter(adapter);
        searchTextBox.showDropDown();
        Log.i("setAdapter","true");
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
            response = requestHandler.handle(getString(R.string.URL_SEARCH_USER), "POST", paramsMap);
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
                    uidMap = new HashMap<>();
                    List<String> adapterList = new ArrayList<>();
                    JSONArray users = response.getJSONArray("data").getJSONArray(0);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        String id = user.getString("uid");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        adapterList.add(new String(id));
                        uidMap.put(id,id);
                        uidMap.put(name,id);
                        uidMap.put(email,id);
                    }
                    setAdapter(adapterList);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            searchUserTask = null;
        }

    }

}
