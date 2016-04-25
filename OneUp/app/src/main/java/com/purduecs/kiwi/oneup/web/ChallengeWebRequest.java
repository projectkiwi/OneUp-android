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
        headerArgs.put("token", RequestQueueSingleton.AUTH_TOKEN);

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
            Log.d(TAG, response.toString());
            c.id = response.getString("_id");
            temp = c.id;

            JSONArray attempts = response.getJSONArray("attempts");
            //JSONArray holders = response.getJSONArray("record_holders");

            c.name = response.getString("name");
            if (attempts.length() > 0) {
                c.image = OneUpWebRequest.BASE_URL + "/" + attempts.getJSONObject(attempts.length()-1).getString("gif_img");
                c.previewImage = OneUpWebRequest.BASE_URL + "/" + attempts.getJSONObject(attempts.length()-1).getString("gif_img");
                c.owner = attempts.getJSONObject(attempts.length()-1).getJSONObject("user").getString("username").split("@")[0];
                //c.owner = attempts.getJSONObject(attempts.length()-1).getJSONObject("user").getString("email").split("@")[0];
                if (attempts.getJSONObject(attempts.length()-1).has("orig_video")) {
                    //c.video = OneUpWebRequest.BASE_URL + "/" + attempts.getJSONObject(attempts.length() - 1).getString("orig_video");
                }
            } else {
                c.image = "nope";
                c.previewImage = "nope";
                c.owner = "NONE";
            }

            c.categories = response.getJSONArray("categories").toString()
                    .replace("\"", "").replace("[", "").replace("]", "").split(",");c.score = 164;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                c.time = format.parse(response.getString("updated_on"));
            } catch (ParseException e) {
                e.printStackTrace();
                c.time = new Date();
            }
            c.desc = response.getString("description");//"lots of placeholder text yo so this looks like a pretty high quality description";

            c.likes = response.getInt("challenge_likes");//103;
            c.liked = (response.getBoolean("liked_top_attempt") ? 1 : 0)
                        + (response.getBoolean("liked_previous_attempt") ? 2 : 0);
            c.bookmarked = response.getBoolean("bookmarked_challenge");

            //LOCATION UNCOMMENT HERE
            //c.latitude = response.getJSONArray("location").getJSONObject(0).toString();
            //c.longitude = response.getJSONArray("location").getJSONObject(1).toString();

            // now add attempts
            c.attempts = new Attempt[attempts.length()];
            for (int i = 0; i < attempts.length(); i++) {
                JSONObject a = attempts.getJSONObject(attempts.length() - i - 1);
                //JSONObject h = holders.getJSONObject(attempts.length() - i - 1);
                c.attempts[i] = new Attempt();
                c.attempts[i].id = a.getString("_id");
                c.attempts[i].image = OneUpWebRequest.BASE_URL + "/" + a.getString("gif_img");
                c.attempts[i].gif = OneUpWebRequest.BASE_URL + "/" + a.getString("gif_img");
                if (a.has("orig_video")) {
                    //c.attempts[attempts.length()-i-1].video = OneUpWebRequest.BASE_URL + "/" + a.getString("orig_video");
                }
                try {
                    c.attempts[i].time = format.parse(a.getString("created_on"));
                } catch (ParseException e) {
                    e.printStackTrace();
                    c.attempts[i].time = new Date();
                }
                c.attempts[i].number = 1234;
                c.attempts[i].desc = a.getString("description");
                c.attempts[i].likes_num = a.getInt("like_total");
                c.attempts[i].has_liked = a.getBoolean("liked_attempt");
                c.attempts[i].owner = a.getJSONObject("user").getString("username");
                //c.attempts[i].owner = a.getJSONObject("user").getString("email").split("@")[0];
                c.attempts[i].place = i+1;
            }
            if (c.attempts.length > 0)
                c.attempt_id = c.attempts[0].id;
            else
                c.attempt_id = "nope";

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
