package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONObject;

public class LikeWebRequest implements OneUpWebRequest<JSONObject, Boolean> {

    Request request;
    private static String TAG = "OneUP";

    public LikeWebRequest(String challengeId, boolean liked, final RequestHandler<Boolean> handler) {

        JSONObject post = new JSONObject();
        try {
            post.put("id", challengeId);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when making a login posting json object");
        }

        String url = liked ? "/like/" : "/dislike/";

        // Now POST that object
        request = new JsonObjectRequest(Request.Method.POST,
                OneUpWebRequest.BASE_URL + url,
                post,
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
    public Boolean parseResponse(JSONObject response) {
        boolean liked = true;
        try {
            liked = response.getBoolean("success");
        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting return in LikeWebRequest");
        }
        return liked;
    }

    @Override
    public boolean cancelRequest() {
        if (request.isCanceled()) return false;
        request.cancel();
        return true;
    }
}
