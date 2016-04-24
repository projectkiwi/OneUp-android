package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONObject;

import java.util.Map;


public class UserGetWebRequest implements OneUpWebRequest<JSONObject, String> {

    Request mRequest;
    private static String TAG = "OneUP";

    public UserGetWebRequest(final RequestHandler<String> handler) {

        Map<String, String> headerArgs = new ArrayMap<String, String>();
        headerArgs.put("token", RequestQueueSingleton.AUTH_TOKEN);

        // Now post that object
        mRequest = new JsonObjectEditHeaderRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/me",
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

        RequestQueueSingleton.getInstance(OneUpApplication.getAppContext()).addToRequestQueue(mRequest);
    }

    @Override
    public String parseResponse(JSONObject response) {
        // May need to check response to see if we got success or failure back
        try {
            //Log.d(TAG, "PUT Username " + response.getString("avatar"));
            return response.getString("avatar");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean cancelRequest() {
        if (mRequest.isCanceled()) return false;
        mRequest.cancel();
        return true;
    }
}
