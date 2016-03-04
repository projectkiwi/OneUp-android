package com.purduecs.kiwi.oneup.web;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Adam on 2/25/16.
 */
public class ChallengesWebRequest implements OneUpWebRequest<JSONArray, ArrayList<Challenge>> {

    public ChallengesWebRequest(String type, final RequestHandler<ArrayList<Challenge>> handler) {
        Request request = new JsonArrayRequest(OneUpWebRequest.BASE_URL + "/challenges",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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

    String[] winners = new String[] {"loeb", "mkausas", "dalal", "semenza", "christiansen", "craven"};
    java.util.Random r = new java.util.Random();

    @Override
    public ArrayList<Challenge> parseResponse(JSONArray response) {
        ArrayList<Challenge> c = new ArrayList<Challenge>();
        try {
            int ind = 0;
            JSONObject chall;
            Challenge challe;
            while (!response.isNull(ind)) {
                chall = response.getJSONObject(ind++);
                challe = new Challenge();
                challe.name = chall.getString("name");
                challe.id = R.drawable.doge_with_sunglasses;
                challe.categories = chall.getJSONArray("categories").toString()
                        .replace("\"", "").replace("[", "").replace("]", "").split(",");
                challe.owner = winners[r.nextInt(winners.length)];
                challe.score = r.nextInt(1000);
                challe.time = r.nextInt(10);
                challe.desc = "lots of placeholder text yo so this looks like a pretty high quality description";
                c.add(challe);
            }
        } catch (Exception e) {

        }
        return c;
    }

    private static Challenge[] toChallengesArray(ArrayList<Challenge> challenges)
    {
        Challenge[] ret = new Challenge[challenges.size()];
        Iterator<Challenge> iterator = challenges.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next();
        }
        return ret;
    }
}
