package com.example.sib.finalproject.fragments;

/**
 * Created by Rameen Barish on 4/29/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sib.finalproject.LoginRestClient;
import com.example.sib.finalproject.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView profileName, longData, latData;
    private Button mapsButton;
    private ImageView compassArrow;
    private SharedPreferences app_preferences;
    private LoginRestClient loginClient;
    private Button maps;
    private double userLat;
    private double userLong;
    Float thisLatt;
    Float thisLongg;

    String this_username;
    public static final String TAG_PROFILE_FRAGMENT = "profile_fragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userLat = 0;
        userLong = 0;
        Log.d("ProfileFragment:", "Messages Fragment ON_CREATE_VIEW()");
        View view = inflater.inflate(R.layout.profile_activity, container, false);
        loginClient = new LoginRestClient();
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = app_preferences.edit();

        userLat = Double.valueOf(app_preferences.getString("lat", "0"));
        userLong =Double.valueOf(app_preferences.getString("long", "0"));

        this_username = app_preferences.getString("clicked_user", "");
        profileName = (TextView) view.findViewById(R.id.profileName);
        longData = (TextView) view.findViewById(R.id.longData);
        latData = (TextView)view.findViewById(R.id.latData);
        maps = (Button) view.findViewById(R.id.map);
        maps.setOnClickListener(this);
        RequestParams params = new RequestParams();
        params.put("name", this_username);
        loginClient.post("getcoord", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("success")) {
                        Toast.makeText(getActivity(), "user not found",
                                Toast.LENGTH_LONG).show();
                    } else {
                         thisLatt = Float.valueOf(response.getString("lat"));
                        latData.setText("" + (thisLatt));
                         thisLongg = Float.valueOf(response.getString("long"));
                        Log.d("LAT", "" + thisLatt);
                        longData.setText("" + (thisLongg));
                        profileName.setText(this_username);
                        Location ogLoc = new Location("");
                        ogLoc.setLongitude(userLong);
                        ogLoc.setLatitude(userLat);

//                        Location thisLoc = new Location("");
//                        thisLoc.setLongitude(thisLongg);
//                        thisLoc.setLatitude(thisLatt);
//
//                        float dist = ogLoc.distanceTo(thisLoc);
//                        Log.d("DIST", "" + dist);
//
//                        distanceData.setText("" + dist);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("ASYNC", " done");
            }

        });




        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.map) {
            Log.d("MAPS", "CLICKED FUCKING FLJKHKJF");
            String uri = "http://maps.google.com/maps?saddr=" + thisLatt + "," + thisLongg;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }
}