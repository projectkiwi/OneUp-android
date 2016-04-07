package com.purduecs.kiwi.oneup.web;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Adam on 3/3/16.
 */
public class ChallengeWebRequest implements OneUpWebRequest<JSONObject, Challenge> {

    Request mRequest;
    private static String TAG = "OneUP";

    public ChallengeWebRequest(String challengeId, final RequestHandler<Challenge> handler) {

        // Now get that object
        mRequest = new JsonObjectRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/challenges/" + challengeId,
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
    public Challenge parseResponse(JSONObject response) {
        Challenge c = new Challenge();
        try {
            c.id = response.getString("_id");
            c.attempt_id = response.getJSONArray("attempts").getJSONObject(0).getString("_id");
            c.name = response.getString("name");
            c.image = response.getJSONArray("attempts").getJSONObject(0).getString("gif_img");
            c.categories = response.getJSONArray("categories").toString()
                    .replace("\"", "").replace("[", "").replace("]", "").split(",");
            c.owner = "temp";
            c.score = 164;
            c.time = "1 d";
            c.desc = response.getString("description");//"lots of placeholder text yo so this looks like a pretty high quality description";
            c.previewImage = response.getJSONArray("attempts").getJSONObject(0).getString("preview_img");
            c.likes = response.getInt("challenge_likes");//103;
            c.liked = 0;
        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting individual challenge in ChallengeWebRequest");
        }
        return c;
    }

    @Override
    public boolean cancelRequest() {
        if (mRequest.isCanceled()) return false;
        mRequest.cancel();
        return true;
    }
}
