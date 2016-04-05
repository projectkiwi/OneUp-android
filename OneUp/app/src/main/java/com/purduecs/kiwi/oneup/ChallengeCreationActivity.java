package com.purduecs.kiwi.oneup;

/* Challenge Creation Activity : Empty Activity

 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.ChallengePostWebRequest;
import com.purduecs.kiwi.oneup.web.OneUpWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;

public class ChallengeCreationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{
    public static final int REQUEST_POST = 12;
    public static final String EXTRA_ID = "com.purduecs.kiwi.oneup.extra_id";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int SELECT_PHOTO_ACTIVITY_REQUEST_CODE = 300;

    private static String TAG = "OneUP";

    private Bitmap challenge_pic;

    public static Intent intentFor(Context context) {
        return new Intent(context, ChallengeCreationActivity.class);
    }

    TextView nameField;
    TextView descField;
    TextView catField;

    GoogleApiClient googleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_creation);
        setUpActionBar();

        nameField = (TextView)findViewById(R.id.challenge_name);
        descField = (TextView)findViewById(R.id.challenge_desc);
        catField = (TextView)findViewById(R.id.challenge_categories);

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge_creation, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Check damned permission
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Toast.makeText(this, String.format("" + lastLocation.getLatitude() + "," + lastLocation.getLongitude()), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "No Permission for Location", Toast.LENGTH_LONG).show();
        }
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void onConnectionSuspended(int i) {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void uploadChallenge(MenuItem menuItem) {
        OneUpWebRequest r = new ChallengePostWebRequest(getChallenge(), new RequestHandler<String>() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    Intent result = new Intent();
                    result.putExtra(EXTRA_ID, response);
                    setResult(Activity.RESULT_OK, result);
                    finish();
                }
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Failed to post challenge");
            }
        });
    }

    private Challenge getChallenge() {
        Challenge c = new Challenge();
        c.name = nameField.getText().toString();
        c.owner = "loeb";
        c.desc = descField.getText().toString();
        c.categories = catField.getText().toString().split(",");
        c.pattern = "pattern yo";
        return c;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                Bundle extras = data.getExtras();
                challenge_pic = (Bitmap) extras.get("data");
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }

        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }

        } else if(requestCode == SELECT_PHOTO_ACTIVITY_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                challenge_pic = BitmapFactory.decodeFile(filePath);
                Toast.makeText(ChallengeCreationActivity.this, "File Path = " + filePath, Toast.LENGTH_LONG).show();
            }
        }

        if(resultCode == RESULT_OK) {
            ImageButton button = (ImageButton) findViewById(R.id.challenge_media_button);
            button.setImageBitmap(challenge_pic);
        }
    }

    //OnClick Listener for imagebutton
    public void selectMedia(View v) {
        final CharSequence[] items = { "Take Photo", "Take Video", "Photo from Gallery","Video from Gallery", "Cancel" };

        AlertDialog.Builder media_sel = new AlertDialog.Builder(ChallengeCreationActivity.this);
        media_sel.setTitle("Select Media");
        media_sel.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //TODO: Store based on TimeStamp
                    File media_folder = new File(Environment.getExternalStorageDirectory(), "OneUp");
                    media_folder.mkdirs();
                    File image = new File(media_folder, "hello_world.jpg");
                    Uri uriSavedImage = Uri.fromFile(image);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Take Video")) {

                    /*
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    //TODO: Store based on TimeStamp
                    File media_folder = new File(Environment.getExternalStorageDirectory(), "OneUp");
                    media_folder.mkdirs();
                    File image = new File(media_folder, "hello_world.mp4");
                    Uri uriSavedImage = Uri.fromFile(image);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
                    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                    */

                    Toast.makeText(ChallengeCreationActivity.this, "This feature coming soon!\n", Toast.LENGTH_LONG).show();

                } else if (items[item].equals("Photo from Gallery")) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Video from Gallery")) {

                    Toast.makeText(ChallengeCreationActivity.this, "This feature coming soon!\n", Toast.LENGTH_LONG).show();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        media_sel.show();
    }
}


