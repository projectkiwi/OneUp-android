package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONObject;

public class LoginPostUsernameWebRequest implements OneUpWebRequest<JSONObject, String> {

    Request request;
    private static String TAG = "OneUP";

    public LoginPostUsernameWebRequest(String email, String auth_tok, final RequestHandler<String> handler) {

        JSONObject post = new JSONObject();
        try {
            post.put("email", email);
            post.put("access_token", auth_tok);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when making a login username posting json object");
        }

        // Now POST that object
        request = new JsonObjectRequest(Request.Method.POST,
                OneUpWebRequest.BASE_URL + "/auth/facebook",
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
    public String parseResponse(JSONObject response) {
        try {
            return response.getJSONObject("user").getString("username");
        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting return in LoginPostUsername");
            return null;
        }
    }

    @Override
    public boolean cancelRequest() {
        if (request.isCanceled()) return false;
        request.cancel();
        return true;
    }
}
