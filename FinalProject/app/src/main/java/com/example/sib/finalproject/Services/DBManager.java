package com.example.sib.finalproject.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.sib.finalproject.Compass;
import com.example.sib.finalproject.LoginActivity;
import com.example.sib.finalproject.LoginRestClient;
import com.example.sib.finalproject.MainActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class DBManager extends Service {
    Compass dbCompass;
    LoginRestClient client;
    String username;
    double lat;
    double longg;
    private SharedPreferences app_preferences;

    public DBManager() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        // we need this for extracting username when 'emitting' steps to the server.
        //prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username  = app_preferences.getString("userId", "");
        dbCompass = new Compass();
        // We need to initialize an intent filter that will recognize 'ACTION_TIME_TICK'
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        client = new LoginRestClient();
        // We need to register our local broadcast receiver
        registerReceiver(receiver, filter);

        Log.d("background_service", "BackgroundService Started!");


        //sensor manager allows us to get access to all of the sensors that your device is offering you.
        //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // START_STICKY -- ? what does it mean? Research it.
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("background_service", "BackgroundService Stopped!");

        //inside onDestroy you need to 'unregister' the broadcast receiver
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLongg(double longg) {
        this.longg = longg;
    }

    public double getLat() {
        return lat;
    }

    public double getLongg() {
        return longg;
    }

    // BroadcastRecevier receiver
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // extracing the string that the action is bringin in
            String action = intent.getAction();
            //get data
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            long myLat = Long.valueOf(preferences.getString("lat", "0"));
            long myLong = Long.valueOf(preferences.getString("long", "0"));

            Log.d("broadcast_service", "action received:" + action.toString());


            // if our action contains "TIME_TICK" we upload to the server via socket
            if (action.contains("TIME_TICK")) {
                // Location current = dbCompass.getCurrentLlocation();

                if (myLong != 0 && myLat != 0) {
                    LoginRestClient client = new LoginRestClient();
                    RequestParams params = new RequestParams();
                    params.put("name", username);
                    params.put("lat", myLat);
                    params.put("long", myLong);
                    client.post("updatelocation", params, new JsonHttpResponseHandler() {
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                            try {
                                if (response.has("success")) {

                                    Log.d("DBManage", "username not found/ password does not match");
                                } else {


                                    Log.d("u", "Loation Updated " + response.getString("name"));
                                }
                                //Log.d("LOCATION", "Updated" + String.valueOf(lastKnownLocation.getLatitude()));
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    Log.d("DBMANAGER", "the current is null");
                }

            }




        }

    };

    public void setCompass (Compass comp) {
        this.dbCompass = comp;
    }
}
