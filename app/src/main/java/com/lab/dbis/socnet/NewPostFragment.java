package com.lab.dbis.socnet;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPostFragment extends Fragment {

    private String SessionID;
    private static int GET_POST_IMAGE = 1;
    private View view;
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
        view = inflater.inflate(R.layout.fragment_new_post, container, false);
        SessionID = getArguments().getString("SessionID");

        final EditText addPostEditText = (EditText) view.findViewById(R.id.edittext_new_post);
        final Button addPostButton = (Button) view.findViewById(R.id.button_post_new_post);
        final Button addImageButton = (Button) view.findViewById(R.id.button_post_picture);
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
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("location", "SeePosts");
                bundle.putString("SessionID", SessionID);
                ViewPostFragment newFragment = new ViewPostFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_placeholder, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        addImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GET_POST_IMAGE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_POST_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) view.findViewById(R.id.image_new_post);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(selectedImage);

        }


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

