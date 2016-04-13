package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationGetWebRequest implements OneUpWebRequest<JSONArray, ArrayList<ArrayList<String>>> {

    Request request;
    private static String TAG = "OneUP";

    public LocationGetWebRequest(String lat, String lon, final RequestHandler<ArrayList<ArrayList<String>>> handler) {

        // Now POST that object
        request = new JsonArrayRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/locations?lat=" + lat + "&lon=" + lon,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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
    public ArrayList<ArrayList<String>> parseResponse(JSONArray response) {
        ArrayList arrayList = new ArrayList();
        JSONObject j;
        try {
            for (int i = 0; i < response.length(); i++) {
                j = response.getJSONObject(i);
                ArrayList a = new ArrayList();
                a.add(j.getString("name"));
                Log.d(TAG, i + "th name is : " + j.getString("name"));
                a.add(j.getString("_id"));
                arrayList.add(a);
            }

        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting return in LocationGET");
        }
        return arrayList;
    }

    @Override
    public boolean cancelRequest() {
        if (request.isCanceled()) return false;
        request.cancel();
        return true;
    }

}
