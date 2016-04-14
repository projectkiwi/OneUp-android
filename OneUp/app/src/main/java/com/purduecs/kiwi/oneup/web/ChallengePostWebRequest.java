package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class ChallengePostWebRequest implements OneUpWebRequest<JSONObject, String> {

    Request mRequest;
    private static String TAG = "OneUP";

    public ChallengePostWebRequest(Challenge challenge, final RequestHandler<String> handler) {
        // Make the json object to send
        JSONObject post = new JSONObject();
        JSONArray cats = new JSONArray();
        try {
            post.put("name", challenge.name);
            post.put("description", challenge.desc);
            post.put("pattern", challenge.pattern);
            post.put("owner", challenge.owner);
            for (int i = 0; i < challenge.categories.length; i++) {
                cats.put(i, challenge.categories[i]);
                Log.d("HEY", challenge.categories[i]);
            }

            post.put("categories", cats);

        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when making a challenge posting json object");
        }

        Map<String, String> headerArgs = new ArrayMap<String, String>();
        headerArgs.put("token", RequestQueueSingleton.AUTH_TOKEN);

        // Now post that object
        mRequest = new JsonObjectEditHeaderRequest(Request.Method.POST,
                OneUpWebRequest.BASE_URL + "/challenges",
                headerArgs,
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

        RequestQueueSingleton.getInstance(OneUpApplication.getAppContext()).addToRequestQueue(mRequest);
    }

    @Override
    public String parseResponse(JSONObject response) {
        // May need to check response to see if we got success or failure back
        try {
            Log.d(TAG, response.getJSONObject("data").getString("_id"));
            return response.getJSONObject("data").getString("_id");
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
