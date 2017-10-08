package com.lab.dbis.socnet;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPostFragment extends Fragment {

    private String SessionID;
    public NewPostFragment() {
        // Required empty public constructor
    }

    public void toast(final String msg, final int duration){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), msg, duration).show();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);
        SessionID = getArguments().getString("SessionID");

        final EditText addPostEditText = (EditText) view.findViewById(R.id.edittext_new_post);
        final Button addPostButton = (Button) view.findViewById(R.id.button_post_new_post);
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel_new_post);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = addPostEditText.getText().toString();
                if (content.equals(""))
                    return;
                AddPostTask addPostTask = new AddPostTask(SessionID,content);
                addPostTask.execute((Void) null);
            }
        });
        return view;
    }

    private class AddPostTask extends AsyncTask<Void, Void, Boolean> {
        private final String SessionID;
        private final String content;

        AddPostTask(String SessionID, String content) {
            this.SessionID = SessionID;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("content",content);
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.setSessionID(SessionID);
            JSONObject response = requestHandler.handle(getString(R.string.base_url)+"CreatePost", "POST", paramsMap);
            try {
                if (response.getBoolean("status"))
                    return true;
                else {
                    NewPostFragment.this.toast("Not connected to internet", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                NewPostFragment.this.toast("Server error", Toast.LENGTH_SHORT);
            } catch (NullPointerException e){
                e.printStackTrace();
                NewPostFragment.this.toast("Not connected to internet", Toast.LENGTH_SHORT);
            }
            return false;
        }

        @Override
        protected  void onPostExecute(final Boolean success) {
            if (success) {
                NewPostFragment.this.toast("Added post successfully", Toast.LENGTH_SHORT);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
            }
        }

        @Override
        protected  void onCancelled() {

        }

    }

}

