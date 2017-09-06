package com.example.sib.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText user, pass;
    private Button login, register;
    private LoginRestClient loginClient;
    private boolean freez;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById((R.id.password));
        login = (Button) findViewById((R.id.loginButton));
        register = (Button) findViewById((R.id.registerButton));

        freez = false;

        login.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (!freez) {
            freez = true;
            if (v == login) {

                //make param object
                RequestParams params = new RequestParams();
                params.put("name", user.getText().toString());
                params.put("password", pass.getText().toString());
                //then post
                loginClient.post("check", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        try {
                            if (response.has("success")) {
                                Toast.makeText(LoginActivity.this, "Username/Password does not match",
                                        Toast.LENGTH_LONG).show();
                                Log.d("LOGIN", "username not found/ password does not match");
                            } else {
                                SharedPreferences app_preferences = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = app_preferences.edit();

                                editor.putString("userId", user.getText().toString());
                                editor.putString("loggedin", "yes");
                                editor.putString("password", pass.getText().toString());
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                JSONArray friends_list = response.getJSONArray("friends");
                                ArrayList<String> friends = new ArrayList<String>();
                                for(int i = 0; i < friends_list.length(); i++) {
                                    friends.add(friends_list.getString(i));
                                }
                                intent.putStringArrayListExtra("friends_list", friends);

                                startActivity(intent);
                                finish();


                                Log.d("LOGIN:", "SUCCESS, the username was " + response.getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });
            } else {
                RequestParams params = new RequestParams();
                params.put("name", user.getText().toString());
                params.put("password", pass.getText().toString());

                //then post
                loginClient.post("register", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        try {
                            if (response.has("success")) {
                                Log.d("REGISTER", "username already exists");
                                Toast.makeText(LoginActivity.this, "Username already exists",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Log.d("REGISTER:", "SUCCESS, user: " + response.getString("name") + " created");
                                Toast.makeText(LoginActivity.this, "new user " + response.getString("name") + " created",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });

            }
            freez = false;
        }
    }

    /**
     * Created by Shuo on 3/22/2017.
     */

    public static class DrillfieldCompass {
        public void setAngle(double angle) {
            this.angle = angle;
        }

        public void setCurrentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
        }

        double angle;
        Location currentLocation;
        Location drillfieldLocation;

        public DrillfieldCompass() {
            drillfieldLocation = new Location("");
            drillfieldLocation.setLatitude(37.227429);
            drillfieldLocation.setLongitude(-80.422230);
        }

        public double getDrillfieldAngle() {
            if (currentLocation != null) {
                if (currentLocation != null) {
                    double mapAngle = currentLocation.bearingTo(drillfieldLocation);
                    if (mapAngle < 0) {
                        mapAngle += 360;
                    }
                    return mapAngle - angle;
                }
            }
            return 0;
        }

        public Location getDrillfieldDistance() {
            if (currentLocation != null) {
                return currentLocation;
            }
            return null;
        }
    }
}
