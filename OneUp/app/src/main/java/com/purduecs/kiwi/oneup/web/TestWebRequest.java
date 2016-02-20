package com.purduecs.kiwi.oneup.web;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.purduecs.kiwi.oneup.OneUpApplication;

import org.json.JSONObject;

/**
 * Created by Adam on 2/18/16.
 */
public class TestWebRequest implements OneUpWebRequest<JSONObject, TestWebRequest.Test> {

    public TestWebRequest(final RequestHandler<Test> handler) {
        Request request = new JsonObjectRequest("http://pastebin.com/raw/U1sADmGd"/*OneUpWebRequest.BASE_URL + "/test"*/,
                new JSONObject(),
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
    public Test parseResponse(JSONObject response) {
        Test test = new Test();
        try {
            test.one = response.getInt("one");
            test.two = response.getInt("two");
        } catch (Exception e) {

        }
        return test;
    }

    public class Test {
        public int one;
        public int two;
    }
}
