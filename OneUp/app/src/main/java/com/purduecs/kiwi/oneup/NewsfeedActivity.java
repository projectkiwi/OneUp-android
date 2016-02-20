package com.purduecs.kiwi.oneup;

/* Newsfeed Activity : Scrolling Activity

 */


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.purduecs.kiwi.oneup.web.RequestHandler;
import com.purduecs.kiwi.oneup.web.TestWebRequest;

public class NewsfeedActivity extends OneUpActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mTextView = (TextView) findViewById(R.id.tempTextYo);
        Button makeRequest = (Button) findViewById(R.id.makeRequestButton);
        makeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TestWebRequest(new RequestHandler<TestWebRequest.Test>() {
                    @Override
                    public void onSuccess(TestWebRequest.Test response) {
                        mTextView.setText("got:\none: "+response.one +"\ntwo: "+response.two);
                    }

                    @Override
                    public void onFailure() {
                        mTextView.setText("failed");
                    }
                });
            }
        });
    }
}
