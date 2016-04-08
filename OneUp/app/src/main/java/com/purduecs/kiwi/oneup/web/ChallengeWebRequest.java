package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.text.format.Time;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Attempt;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Adam on 3/3/16.
 */
public class ChallengeWebRequest implements OneUpWebRequest<JSONObject, Challenge> {

    Request mRequest;
    private static String TAG = "OneUP";

    public ChallengeWebRequest(String challengeId, final RequestHandler<Challenge> handler) {

        Map<String, String> headerArgs = new ArrayMap<String, String>();;
        headerArgs.put("userid", "57065ffb81b46b7c289a6144");

        // Now get that object
        mRequest = new JsonObjectEditHeaderRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/challenges/" + challengeId,
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
    public Challenge parseResponse(JSONObject response) {
        Challenge c = new Challenge();
        String temp = "x";
        try {
            c.id = response.getString("_id");
            temp = c.id;

            JSONArray attempts = response.getJSONArray("attempts");

            c.name = response.getString("name");
            c.image = attempts.getJSONObject(0).getString("gif_img");
            c.categories = response.getJSONArray("categories").toString()
                    .replace("\"", "").replace("[", "").replace("]", "").split(",");
            c.owner = "temp";
            c.score = 164;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                c.time = format.parse(response.getString("updated_on"));
            } catch (ParseException e) {
                e.printStackTrace();
                c.time = new Date();
            }
            c.desc = response.getString("description");//"lots of placeholder text yo so this looks like a pretty high quality description";
            c.previewImage = response.getJSONArray("attempts").getJSONObject(0).getString("preview_img");
            c.likes = response.getInt("challenge_likes");//103;
            c.liked = 0;
            c.bookmarked = false;

            // now add attempts
            c.attempts = new Attempt[attempts.length()];
            for (int i = 0; i < attempts.length(); i++) {
                JSONObject a = attempts.getJSONObject(i);
                c.attempts[i] = new Attempt();
                c.attempts[i].id = a.getString("_id");
                c.attempts[i].image = a.getString("preview_img");
                c.attempts[i].gif = a.getString("gif_img");
                try {
                    c.attempts[i].time = format.parse(a.getString("created_on"));
                } catch (ParseException e) {
                    e.printStackTrace();
                    c.attempts[i].time = new Date();
                }
                c.attempts[i].number = 1234;
                c.attempts[i].desc = "people";
                c.attempts[i].likes_num = a.getInt("like_total");
                c.attempts[i].has_liked = false;
                c.attempts[i].owner = "adam";
                c.attempts[i].place = i+1;
            }
            if (c.attempts.length > 0)
                c.attempt_main = c.attempts[0];

        } catch (Exception e) {
            Log.e(TAG, "Had an issue parsing JSON when getting individual challenge in ChallengeWebRequest - " + e.getMessage());
            c.id = temp;
            c.debug_flag = 1;
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
