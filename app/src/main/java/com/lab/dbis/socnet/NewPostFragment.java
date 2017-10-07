package com.lab.dbis.socnet;


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
            JSONObject response = requestHandler.handle(getString(R.string.URL_ADD_POST), "POST", paramsMap);
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
            if (success)
                Log.i("AddPost","Posted");
            //Toast.makeText(getApplicationContext(),"You are not connected to the internet",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected  void onCancelled() {

        }

    }

}

