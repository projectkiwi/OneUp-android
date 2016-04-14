package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.text.format.Time;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Adam on 2/25/16.
 */
public class ChallengesWebRequest implements OneUpWebRequest<JSONObject, ArrayList<Challenge>> {

    Request mRequest;
    private static String TAG = "OneUP";

    public ChallengesWebRequest(String type, int start, int length, final RequestHandler<ArrayList<Challenge>> handler) {

        switch (type) {
            case "new":
            case "popular":
                type = "/challenges/local/" + type;
                break;
            case "bookmarks":
                type = "/bookmarks/";
                break;
            case "global":
            default:
                type = "/challenges/";
                break;
        }

        Map<String, String> headerArgs = new ArrayMap<String, String>();
        headerArgs.put("offset", Integer.toString(start));
        headerArgs.put("limit", Integer.toString(length));
        headerArgs.put("token", RequestQueueSingleton.AUTH_TOKEN);

        mRequest = new JsonObjectEditHeaderRequest(Request.Method.GET, OneUpWebRequest.BASE_URL + type, headerArgs, null,
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

    String[] winners = new String[] {"loeb", "mkausas", "dalal", "semenza", "christiansen", "craven"};
    java.util.Random r = new java.util.Random();

    @Override
    public ArrayList<Challenge> parseResponse(JSONObject response2) {

        JSONArray response = null;
        try {
            response = response2.getJSONArray("docs");
        } catch (Exception e) {
            ;
        }

        ArrayList<Challenge> c = new ArrayList<Challenge>();
        try {
            int ind = 0;
            JSONObject chall;
            Challenge challe;
            while (ind < response.length()) {
                chall = response.getJSONObject(ind++);
                challe = new Challenge();
                challe.id = chall.getString("_id");

                JSONArray attempts = chall.getJSONArray("attempts");
                if(!chall.has("name"))
                    continue;
                if (attempts.length() > 0) {
                    challe.attempt_id = attempts.getJSONObject(attempts.length() - 1).getString("_id");
                    challe.image = OneUpWebRequest.BASE_URL + "/" + attempts.getJSONObject(attempts.length() - 1).getString("gif_img");
                    challe.previewImage = OneUpWebRequest.BASE_URL + "/" + attempts.getJSONObject(attempts.length() - 1).getString("gif_img");
                    //JSONArray holders = chall.getJSONArray("record_holders");
                    challe.owner = "test";//holders.getJSONObject(holders.length()-1).getString("email").split("@")[0];
                } else {
                    challe.attempt_id = "nope";
                    challe.image = "nope";
                    challe.previewImage = "nope";
                    challe.owner = "NO ONE";
                }
                challe.name = chall.getString("name");

                challe.categories = chall.getJSONArray("categories").toString()
                        .replace("\"", "").replace("[", "").replace("]", "").split(",");
                challe.score = r.nextInt(1000);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    challe.time = format.parse(chall.getString("updated_on"));
                } catch (ParseException e) {
                    e.printStackTrace();
                    challe.time = new Date();
                }
                challe.desc = chall.getString("description");//"lots of placeholder text yo so this looks like a pretty high quality description";
                challe.likes = chall.getInt("challenge_likes");//r.nextInt(1000);
                challe.liked = (chall.getBoolean("liked_top_attempt") ? 1 : 0)
                        + (chall.getBoolean("liked_previous_attempt") ? 2 : 0);//r.nextInt(3);
                challe.bookmarked = false;
                c.add(challe);
            }
        } catch (Exception e) {
            e.printStackTrace();
                Log.e(TAG, "Something happened in the ChallengeS (plural) request");
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
