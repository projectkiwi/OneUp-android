package com.purduecs.kiwi.oneup.web;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.purduecs.kiwi.oneup.OneUpApplication;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.models.Notification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class NotificationsWebRequest implements OneUpWebRequest<JSONArray, ArrayList<Notification>> {

    Request mRequest;

    public NotificationsWebRequest(final RequestHandler<ArrayList<Notification>> handler) {

        Map<String, String> headerArgs = new ArrayMap<String, String>();;
        headerArgs.put("token", RequestQueueSingleton.AUTH_TOKEN);

        // Now get that object
        mRequest = new JsonArrayEditHeaderRequest(Request.Method.GET,
                OneUpWebRequest.BASE_URL + "/me/notifications",
                headerArgs,
                null,
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

        RequestQueueSingleton.getInstance(OneUpApplication.getAppContext()).addToRequestQueue(mRequest);
    }

    @Override
    public ArrayList<Notification> parseResponse(JSONArray response) {
        ArrayList<Notification> list = new ArrayList<>();

        try {
            for (int i = 0; i < response.length(); i++) {
                Notification n = new Notification();
                n.user = response.getJSONObject(i).getJSONObject("from").getString("username");
                n.desc = response.getJSONObject(i).getString("text");
                n.challenge_id = response.getJSONObject(i).getJSONObject("challenge").getString("_id");
                n.image = "http://doge2048.com/img/212/doge-sunglasses-212.gif";//R.drawable.doge_with_sunglasses;//response.getJSONObject(i).getJSONObject("challenge").getJSONArray("attempts").getJSONObject()

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    n.time = format.parse(response.getJSONObject(i).getString("date"));
                } catch (Exception e) {
                    Log.e("HEY", "Exception in parsing time in notification " + e.toString() + " " + e.getMessage());
                    n.time = new Date();
                }

                list.add(n);
            }
        } catch (Exception e) {
            Log.d("HEY", "Error parsing notifications request");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean cancelRequest() {
        if (mRequest.isCanceled()) return false;
        mRequest.cancel();
        return true;
    }
}
