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
import android.support.v4.app.ActivityCompat;
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
    private static final int SELECT_VIDEO_ACTIVITY_REQUEST_CODE = 300;
    private static final int SELECT_PHOTO_ACTIVITY_REQUEST_CODE = 400;

    private static String TAG = "OneUP";

    private Bitmap challenge_pic;
    private Uri uriSavedImage;

    final private int PERMISSION_ACCESS_FINE_LOCATION = 123;
    final private int PERMISSION_ACCESS_CAMERA = 124;
    final private int PERMISSION_READ_STORAGE = 125;
    final private int PERMISSION_WRITE_STORAGE = 126;

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
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        else{
            getLocation();
        }
    }

    private void getLocation() {
        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Toast.makeText(this, String.format("" + lastLocation.getLatitude() + "," + lastLocation.getLongitude()), Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "No permission for Location", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error : No permission for Location detected.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();
                } else {
                    Toast.makeText(this, "No permission for Location", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "User refused to give Location permission");
                }
            } break;
            case PERMISSION_READ_STORAGE: //Do nothing
                break;
            case PERMISSION_WRITE_STORAGE: //Do nothing
                break;
            case PERMISSION_ACCESS_CAMERA: //Do nothing
                break;

            // other 'case' lines to check for other
            // permissions this app might request
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

        ImageButton imgButton = (ImageButton) (findViewById(R.id.challenge_media_button));

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        uriSavedImage.toString(), Toast.LENGTH_LONG).show();
                Log.v(TAG, String.format("Image saved to:\n" + uriSavedImage.toString()));

                try {
                    challenge_pic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriSavedImage);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                imgButton.setImageBitmap(challenge_pic);

            } else {
                Log.e(TAG, "Something happened during Image capture.");
            }

        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        uriSavedImage.toString(), Toast.LENGTH_LONG).show();
                Log.v(TAG, String.format("Video saved to:\n" + uriSavedImage.toString()));

            } else {
                Log.e(TAG, "Something happened during Video capture.");
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
                Log.v(TAG, String.format(TAG, "Image selected from " + filePath));
            }
        } else if (requestCode == SELECT_VIDEO_ACTIVITY_REQUEST_CODE) {
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
                Log.v(TAG, String.format(TAG, "Video selected from " + filePath));
            }
        }
    }

    //OnClick Listener for imagebutton
    public void selectMedia(View v) {
        final CharSequence[] items = { "Take Photo", "Take Video", "Photo from Gallery","Video from Gallery", "Cancel" };

        //Storage permissions
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,},
                    PERMISSION_READ_STORAGE);
        }

        AlertDialog.Builder media_sel = new AlertDialog.Builder(ChallengeCreationActivity.this);
        media_sel.setTitle("Select Media");
        media_sel.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File media_folder = new File(Environment.getExternalStorageDirectory(), "OneUp");
                    media_folder.mkdirs();
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = String.format(tsLong.toString() + ".jpg");
                    File image = new File(media_folder, ts);
                    uriSavedImage = Uri.fromFile(image);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Take Video")) {

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    File media_folder = new File(Environment.getExternalStorageDirectory(), "OneUp");
                    media_folder.mkdirs();
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = String.format(tsLong.toString() + ".mp4");
                    File image = new File(media_folder, ts);
                    uriSavedImage = Uri.fromFile(image);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
                    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Photo from Gallery")) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO_ACTIVITY_REQUEST_CODE);

                } else if (items[item].equals("Video from Gallery")) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("video/*");
                    startActivityForResult(photoPickerIntent, SELECT_VIDEO_ACTIVITY_REQUEST_CODE);


                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        media_sel.show();
    }
}


