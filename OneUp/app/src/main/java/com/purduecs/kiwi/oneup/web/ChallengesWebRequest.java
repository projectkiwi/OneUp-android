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
        headerArgs.put("token", "57065ffb81b46b7c289a6144");

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
            while (!response.isNull(ind)) {
                chall = response.getJSONObject(ind++);
                challe = new Challenge();
                challe.id = chall.getString("_id");
                if(chall.getJSONArray("attempts").length() <= 0)
                    continue;
                challe.attempt_id = chall.getJSONArray("attempts").getJSONObject(0).getString("_id");
                challe.name = chall.getString("name");
                challe.image = chall.getJSONArray("attempts").getJSONObject(0).getString("gif_img");
                challe.categories = chall.getJSONArray("categories").toString()
                        .replace("\"", "").replace("[", "").replace("]", "").split(",");
                challe.owner = winners[r.nextInt(winners.length)];
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
                challe.previewImage = chall.getJSONArray("attempts").getJSONObject(0).getString("preview_img");
                challe.likes = chall.getInt("challenge_likes");//r.nextInt(1000);
                challe.liked = (chall.getBoolean("liked_top_attempt") ? 1 : 0)
                        + (chall.getBoolean("liked_previous_attempt") ? 2 : 0);//r.nextInt(3);
                challe.bookmarked = false;
                c.add(challe);
            }
        } catch (Exception e) {
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
