package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONObject;

import java.util.Map;

public class LocationGetWebRequest implements OneUpWebRequest<JSONObject, Map<String, String>> {

    Request request;
    private static String TAG = "OneUP";
    private Boolean new_user;

    public LocationGetWebRequest(String lat, String lon, final RequestHandler<Map<String, String>> handler) {

        // Now POST that object
        request = new JsonObjectRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/locations?lat=" + lat + "&lon=" + lon,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handler.onSuccess(parseResponse(response));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handler.onFailure();
                    }
                });

        RequestQueueSingleton.getInstance(OneUpApplication.getAppContext()).addToRequestQueue(request);
    }

    @Override
    public Map<String,String> parseResponse(JSONObject response) {
        try {
            new_user = response.getBoolean("new_account");
            RequestQueueSingleton.AUTH_TOKEN = response.getString("token");
            Log.d("HEY", "token: " + RequestQueueSingleton.AUTH_TOKEN);
        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting return in LoginPost");
        }
        return null;
    }

    @Override
    public boolean cancelRequest() {
        if (request.isCanceled()) return false;
        request.cancel();
        return true;
    }

    public Boolean getNew_user(){
        return new_user;
    }
}
