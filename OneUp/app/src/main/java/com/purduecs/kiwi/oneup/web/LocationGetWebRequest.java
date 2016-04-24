package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationGetWebRequest implements OneUpWebRequest<JSONObject, ArrayList<ArrayList<String>>> {

    Request request;
    private static String TAG = "OneUP";

    public LocationGetWebRequest(String lat, String lon, final RequestHandler<ArrayList<ArrayList<String>>> handler) {

        Map<String, String> headerArgs = new ArrayMap<String, String>();
        headerArgs.put("token", RequestQueueSingleton.AUTH_TOKEN);

        // Now POST that object
        request = new JsonObjectEditHeaderRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/locations?lat=" + lat + "&lon=" + lon,
                headerArgs,
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
    public ArrayList<ArrayList<String>> parseResponse(JSONObject response1) {
        ArrayList<ArrayList<String>> arrayList = new ArrayList();
        JSONObject j;
        try {
            JSONArray response = response1.getJSONArray("locations");
            for (int i = 0; i < response.length(); i++) {
                j = response.getJSONObject(i);
                ArrayList<String> a = new ArrayList();
                a.add(j.getString("name"));
                //Log.d(TAG, i + "th name is : " + j.getString("name"));
                a.add(j.getString("_id"));
                arrayList.add(a);
            }

        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting return in LocationGET " + e.getMessage());
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
