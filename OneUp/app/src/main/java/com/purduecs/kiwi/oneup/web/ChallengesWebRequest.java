package com.purduecs.kiwi.oneup.web;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Adam on 2/25/16.
 */
public class ChallengesWebRequest implements OneUpWebRequest<JSONArray, ChallengesWebRequest.Challenge[]> {

    public ChallengesWebRequest(String type, final RequestHandler<Challenge[]> handler) {
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

    @Override
    public Challenge[] parseResponse(JSONArray response) {
        ArrayList<Challenge> c = new ArrayList<Challenge>();
        try {
            int ind = 0;
            JSONObject chall;
            Challenge challe;
            while (!response.isNull(ind)) {
                chall = response.getJSONObject(ind++);
                challe = new Challenge();
                challe.title = chall.getString("name");
                c.add(challe);
            }
        } catch (Exception e) {

        }
        return toChallengesArray(c);
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


    public class Challenge {
        public String id;
        public String title;
        public String owner;
        public String[] categories;
    }
}
