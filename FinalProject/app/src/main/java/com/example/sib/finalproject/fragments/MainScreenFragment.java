package com.example.sib.finalproject.fragments;

/**
 * Created by Rameen Barish on 4/29/2017.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sib.finalproject.LoginRestClient;
import com.example.sib.finalproject.R;
import com.example.sib.finalproject.interfaces.MainScreenInteraction;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MainScreenFragment extends Fragment implements View.OnClickListener{

    public static final String TAG_MAIN_FRAGMENT = "main_fragment";
    private ArrayList<String> friends;
    private LoginRestClient loginClient;
    private SharedPreferences app_preferences;
    private MainScreenInteraction activity;
    //TextView and progressbar variables

    //We will be clicking on the imageviews, thus we need two variables to represent the top left and top right imageviews.
    private int check = 0;
    private TextView name;
    private ImageView profileImage;
    private ListView lv;
    private Button dropMenu, addFriend, removeFriend;
    private String username;
    ArrayAdapter<String> arrayAdapter;

    public static MainScreenFragment newInstance() {
        MainScreenFragment fragment = new MainScreenFragment();
        return fragment;
    }

    public MainScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainScreenInteraction) {
            activity = (MainScreenInteraction) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeScreenInteraction");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main, container, false);


        //initalise the views and add OnclickListeners as needed
        // use view.findViewById()
        dropMenu = (Button) view.findViewById((R.id.dropMenu));
        dropMenu.setOnClickListener(this);

        addFriend = (Button) view.findViewById(R.id.addFriend);
        addFriend.setOnClickListener(this);
        removeFriend = (Button) view.findViewById(R.id.removeFriend);
        removeFriend.setOnClickListener(this);

        name = (TextView)view.findViewById(R.id.nameField);
        profileImage = (ImageView) view.findViewById(R.id.logo2);
        profileImage.setOnClickListener(this);
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
         username = app_preferences.getString("userId", "");
        String loggedin = app_preferences.getString("loggedin", "");
        Log.d("USERNAME ", username);
        if((loggedin.equals("yes"))){
            name.setText(username);
            String url = "user/" + username;
            Log.d("URL ", url);
            Boolean done = false;
            loginClient.get(url, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.has("success")) {
                            Toast.makeText(getActivity(), "user not found",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            //everything to do with adapter must be done in here
                            //going outside fucks with AsyncTask
                            JSONArray friends_list = response.getJSONArray("friends");
                            friends = new ArrayList<String>();
                            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friends);
                            for(int i = 0; i < friends_list.length(); i++) {
                                Log.d("ADDING: ", friends_list.getString(i));
                                friends.add(friends_list.getString(i));
                                lv = (ListView) view.findViewById(R.id.main_list);
                                lv.setAdapter(arrayAdapter);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        SharedPreferences.Editor editor = app_preferences.edit();
                                        String selected = (String)parent.getItemAtPosition(position);

                                        editor.putString("clicked_user",  selected);
                                        Log.d("CLICK", selected);
                                        editor.commit();
                                        activity.changeFragment(ProfileFragment.TAG_PROFILE_FRAGMENT);
                                    }
                                });
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("ASYNC", " done");
                }

            });

//            Log.d("SIZE ", "value:" + friends.size());
//
        }

        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dropMenu:
                refresh();
                break;
            case R.id.addFriend:
                addFriend();
                break;
            case R.id.removeFriend:
                removeFriend();
                break;
            default:
                break;
        }
    }

    private void refresh() {
        loginClient.get("user/" + username, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("success")) {
                        Toast.makeText(getActivity(), "user not found",
                                Toast.LENGTH_LONG).show();
                    } else {
                        //everything to do with adapter must be done in here
                        //going outside fucks with AsyncTask
                        JSONArray friends_list = response.getJSONArray("friends");
                        friends = new ArrayList<String>();
                        for(int i = 0; i < friends_list.length(); i++) {
                            Log.d("ADDING: " , friends_list.getString(i));
                            friends.add(friends_list.getString(i));
                            lv = (ListView)getView().findViewById(R.id.main_list);
                            arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, friends);
                            lv.setAdapter(arrayAdapter);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("ASYNC", " done");

            }

        });

    }



    public void addFriend() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater(null);
        final View dialogView = inflater.inflate(R.layout.custom_layout, null);
        alert.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.name);
        alert.setTitle("Add a Friend");
        alert.setMessage("Enter your friend's username: ");
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO SERVER SHIT HERE //

                RequestParams params = new RequestParams();
                params.put("name", username);
                params.put("friend", edt.getText().toString());
                Log.d("ADD ", username);
                Log.d("ADD", edt.getText().toString());
                // ADD the friend to the server
                loginClient.post("addfriend", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            if (response.has("message")) {
                                try {
                                    if (response.get("message").equals("friend to add not found")) {
                                        Toast.makeText(getActivity(), "This friend does not exist",
                                                Toast.LENGTH_LONG).show();
                                    } else if (response.get("message").equals("already friends")) {
                                        Toast.makeText(getActivity(), "You are already friends",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                //everything to do with adapter must be done in here
                                //going outside fucks with AsyncTask
                                Toast.makeText(getActivity(), "friend added",
                                        Toast.LENGTH_LONG).show();
                                arrayAdapter.add(edt.getText().toString());
                                arrayAdapter.notifyDataSetChanged();

                            }

                        Log.d("ASYNC", " done");

                    }
                });



                // END SERVER SHIT HERE //
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    public void removeFriend() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater(null);
        final View dialogView = inflater.inflate(R.layout.custom_layout, null);
        alert.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.name);
        alert.setTitle("Remove a Friend");
        alert.setMessage("Enter the username of the friend you want to delete: ");
        alert.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO SERVER SHIT HERE //

                RequestParams params = new RequestParams();
                params.put("name", username);
                params.put("friend", edt.getText().toString());
                Log.d("REMOVE ", username);
                Log.d("REMOVE", edt.getText().toString());
                // Remove the friend to the server
                loginClient.post("removefriend", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        if (response.has("message")) {
                            try {
                                if(response.get("message").equals("You cannot add yourself")) {
                                    Toast.makeText(getActivity(), "You cannot add yourself",
                                            Toast.LENGTH_LONG).show();
                                }
                                else if(response.get("message").equals("friend to remove not found")) {
                                    Toast.makeText(getActivity(), "This friend does not exist",
                                            Toast.LENGTH_LONG).show();
                                } else if (response.get("message").equals("you are not friends with this user")) {
                                    Toast.makeText(getActivity(), "you are not friends with this user",
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            //everything to do with adapter must be done in here
                            //going outside fucks with AsyncTask
                            Toast.makeText(getActivity(), "friend removed",
                                    Toast.LENGTH_LONG).show();
                            arrayAdapter.remove(edt.getText().toString());
                            arrayAdapter.notifyDataSetChanged();

                        }

                        Log.d("ASYNC", " done");

                    }
                });
                // END SERVER SHIT HERE //
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


}
