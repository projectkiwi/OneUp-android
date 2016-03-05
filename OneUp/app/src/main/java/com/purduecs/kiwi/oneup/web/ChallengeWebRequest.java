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

    public ChallengeWebRequest(String challengeId, final RequestHandler<Challenge> handler) {

        // Now get that object
        Request request = new JsonObjectRequest(Request.Method.GET,
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

        RequestQueueSingleton.getInstance(OneUpApplication.getAppContext()).addToRequestQueue(request);
    }

    @Override
    public Challenge parseResponse(JSONObject response) {
        Challenge c = new Challenge();
        try {
            response = response.getJSONObject("docs");
        } catch (Exception e) {
            ;
        }
        try {
            c.id = response.getString("_id");
            c.name = response.getString("name");
            c.image = R.drawable.doge_with_sunglasses;
            c.categories = response.getJSONArray("categories").toString()
                    .replace("\"", "").replace("[", "").replace("]", "").split(",");
            c.owner = "temp";
            c.score = 164;
            c.time = 9;
            c.desc = "lots of placeholder text yo so this looks like a pretty high quality description";
        } catch (Exception e) {
            Log.e("HEY", "Had an issue parsing JSON when getting individual challenge in ChallengeWebRequest");
        }
        return c;
    }
}
