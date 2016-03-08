package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Adam on 3/3/16.
 */
public class ChallengePostWebRequest implements OneUpWebRequest<JSONObject, Boolean> {

    Request mRequest;

    public ChallengePostWebRequest(Challenge challenge, final RequestHandler<Boolean> handler) {
        // Make the json object to send
        JSONObject post = new JSONObject();
        JSONArray cats = new JSONArray();
        try {
            post.put("name", challenge.name);
            post.put("desc", challenge.desc);
            post.put("owner", challenge.owner);
            for (int i = 0; i < challenge.categories.length; i++) {
                cats.put(i, challenge.categories[i]);
            }
            post.put("categories", cats);
        } catch (Exception e) {
            Log.e("HEY", "Something went wrong when making a challenge posting json object");
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
    public Boolean parseResponse(JSONObject response) {
        // May need to check response to see if we got success or failure back
        return true;
    }

    @Override
    public boolean cancelRequest() {
        if (mRequest.isCanceled()) return false;
        mRequest.cancel();
        return true;
    }
}
