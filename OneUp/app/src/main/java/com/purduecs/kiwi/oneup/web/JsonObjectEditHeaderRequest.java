package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Adam on 3/8/16.
 */
public class JsonObjectEditHeaderRequest extends JsonObjectRequest {

    Map<String, String> headers;

    public JsonObjectEditHeaderRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectEditHeaderRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    public JsonObjectEditHeaderRequest(int method, String url, Map<String, String> headers, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
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
