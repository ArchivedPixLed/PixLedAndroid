package com.example.paulbreugnot.lightroom.room;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomContextHttpManager {

    private static final String CONTEXT_SERVER_URL = "http://192.168.12.1:8083/";

    RequestQueue requestQueue;
    RoomManagementActivity contextManagementActivity;

    public RoomContextHttpManager(RoomManagementActivity contextManagementActivity) {
        requestQueue = Volley.newRequestQueue(contextManagementActivity);
        this.contextManagementActivity = contextManagementActivity;
    }

    public void retrieveRoomContextState(String room) {
        final String url = CONTEXT_SERVER_URL + "api/rooms/" + room + "/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY","That didn't work!");
            }
        });

        requestQueue.add(stringRequest);

        //get room sensed context
        JsonObjectRequest contextRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id = response.getString("id");
                            int lightLevel = Integer.parseInt(response.getJSONObject("light").get("level").toString());
                            String lightStatus = response.getJSONObject("light").get("status").toString();
                            float noiseLevel = Float.parseFloat(response.getJSONObject("noise").get("level").toString());

                            RoomContextState roomContextState = new RoomContextState(id, lightStatus, lightLevel, noiseLevel);
                            onUpdate(roomContextState);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY","NO RESPONSE from " + url);
                    }
                });
        requestQueue.add(contextRequest);
    }

    public void onUpdate(RoomContextState state) {
        contextManagementActivity.setState(state);
        contextManagementActivity.updateContextView();
    }
}
