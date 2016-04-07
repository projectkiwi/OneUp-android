package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Adam on 4/7/16.
 */
public class JsonArrayEditHeaderRequest extends JsonArrayRequest {
    Map<String, String> headers;

    public JsonArrayEditHeaderRequest(int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonArrayEditHeaderRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public JsonArrayEditHeaderRequest(int method, String url, Map<String, String> headers, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> h = new ArrayMap<String, String>();
        h.putAll(headers);
        return h;
    }
}
