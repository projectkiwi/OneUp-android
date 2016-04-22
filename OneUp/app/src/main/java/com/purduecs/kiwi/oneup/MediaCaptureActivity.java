package com.purduecs.kiwi.oneup;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MediaCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_capture);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.e("HEY", String.format("No Action Bar. Error : " + e.getMessage()));
        }
    }
}
