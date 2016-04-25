package com.purduecs.kiwi.oneup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.ChallengesWebRequest;
import com.purduecs.kiwi.oneup.web.OneUpWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = "OneUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng west_lafayette = new LatLng(40.427536, -86.916966);

        Bitmap icon1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_map_pin_transparent_actual);
        final Bitmap icon = Bitmap.createScaledBitmap(icon1, icon1.getWidth() / 15, icon1.getHeight() / 15, false);
        final HashMap<String, String> hashMap = new HashMap<>();

        OneUpWebRequest oneUpWebRequest = new ChallengesWebRequest("local", 0, 25, new RequestHandler<ArrayList<Challenge>>() {
            @Override
            public void onSuccess(ArrayList<Challenge> response) {
                Challenge c1 = new Challenge();
                for(Challenge c : response) {
                    ///////////////////INITIALIZE MARKERS HERE///////////////////////////
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(c.latitude), Double.parseDouble(c.longitude)))
                            .title(c.name)
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                            .snippet(c.desc));
                    c1 = c;
                    hashMap.put(c.name + c.desc, c.id);
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(c1.latitude), Double.parseDouble(c1.longitude)), 15));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String chall_id = hashMap.get(marker.getTitle() + marker.getSnippet());
                        startActivity(ChallengeDetailActivity.intentFor(MapsActivity.this, chall_id));
                        return false;
                    }
                });
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "Something went wrong in the map.");
            }
        });

    }

}
