package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.models.Attempt;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChallengePostWebRequest implements OneUpWebRequest<JSONObject, String> {

    Request mRequest;
    private static String TAG = "OneUP";

    public ChallengePostWebRequest(Challenge challenge, Attempt attempt, final RequestHandler<String> handler) {
        // Make the json object to send
        JSONObject post = new JSONObject();
        JSONObject at = new JSONObject();
        JSONArray cats = new JSONArray();
        JSONArray attempts = new JSONArray();
        try {
            post.put("name", challenge.name);
            post.put("description", challenge.desc);
            post.put("pattern", challenge.pattern);
            post.put("owner", challenge.owner);
            for (int i = 0; i < challenge.categories.length; i++) {
                cats.put(i, challenge.categories[i]);
            }
            at.put("gif_img", attempt.gif);
            at.put("preview_img", attempt.image);
            at.put("description", attempt.desc);
            post.put("attempts", at);

            post.put("categories", cats);

        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when making a challenge posting json object");
        }

        // Now post that object
        mRequest = new JsonObjectRequest(Request.Method.POST,
                OneUpWebRequest.BASE_URL + "/challenges",
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
