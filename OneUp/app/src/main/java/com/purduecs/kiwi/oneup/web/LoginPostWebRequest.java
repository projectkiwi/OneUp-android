package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONObject;

public class LoginPostWebRequest implements OneUpWebRequest<JSONObject, Boolean> {

    Request request;
    private static String TAG = "OneUP";
    private Boolean new_user;

    public LoginPostWebRequest(String email, String auth_tok, final RequestHandler<Boolean> handler) {

        JSONObject post = new JSONObject();
        try {
            post.put("email", email);
            post.put("access_token", auth_tok);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when making a login posting json object");
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
    public Boolean parseResponse(JSONObject response) {
        new_user = true;
        try {
            new_user = response.getBoolean("new_account");
            RequestQueueSingleton.AUTH_TOKEN = response.getString("token");
            Log.d("HEY", "token: " + RequestQueueSingleton.AUTH_TOKEN);
        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting return in LoginPost");
        }
        return new_user;
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
