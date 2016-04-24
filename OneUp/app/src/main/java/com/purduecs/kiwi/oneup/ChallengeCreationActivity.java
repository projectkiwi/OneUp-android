package com.purduecs.kiwi.oneup;

/* Challenge Creation Activity : Creates an activity, genius

 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.purduecs.kiwi.oneup.models.Attempt;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.AttemptPostWebRequest;
import com.purduecs.kiwi.oneup.web.ChallengePostWebRequest;
import com.purduecs.kiwi.oneup.web.LocationGetWebRequest;
import com.purduecs.kiwi.oneup.web.OneUpWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

public class ChallengeCreationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{
    public static final int REQUEST_POST = 12;
    public static final String EXTRA_ID = "com.purduecs.kiwi.oneup.extra_id";

    private static final String EXTRA_ATTEMPT = "com.purduecs.kiwi.oneup.extra_attempt_bool";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int SELECT_VIDEO_ACTIVITY_REQUEST_CODE = 300;
    private static final int SELECT_PHOTO_ACTIVITY_REQUEST_CODE = 400;

    private static final int TYPE_PICTURE = 0;
    private static final int TYPE_VIDEO = 1;

    private int current_type = -1;

    private static String TAG = "OneUP";

    private Bitmap challenge_pic;
    private Uri uriSavedImage;

    final private int PERMISSION_ACCESS_FINE_LOCATION = 123;
    final private int PERMISSION_ACCESS_CAMERA = 124;
    final private int PERMISSION_READ_STORAGE = 125;
    final private int PERMISSION_WRITE_STORAGE = 126;

    private String responseID = "";
    private String locID = "";

    ///////FOR FUN/////
    Random r;
    int rand;
    String cat;

    public static Intent intentForChallenge(Context context) {
        Intent intent = new Intent(context, ChallengeCreationActivity.class);
        intent.putExtra(EXTRA_ATTEMPT, false);
        return intent;
    }

    public static Intent intentForAttempt(Context context) {
        Intent intent = new Intent(context, ChallengeCreationActivity.class);
        intent.putExtra(EXTRA_ATTEMPT, true);
        return intent;
    }

    boolean isAttempt;

    TextView nameField;
    TextView numField;
    TextView descField;
    TextView catField;
    TextView locField;

    GoogleApiClient googleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_creation);
        setUpActionBar();

        isAttempt = getIntent().getBooleanExtra(EXTRA_ATTEMPT, false);

        nameField = (TextView)findViewById(R.id.challenge_name);
        descField = (TextView)findViewById(R.id.challenge_desc);
        catField = (TextView)findViewById(R.id.challenge_categories);
        numField = (TextView) findViewById(R.id.challenge_num);
        locField = (TextView) findViewById(R.id.challenge_loc);

        if(isAttempt) {
            nameField.setVisibility(View.GONE);
            descField.setVisibility(View.GONE);
            catField.setVisibility(View.GONE);
            locField.setVisibility(View.GONE);
            findViewById(R.id.divider_1).setVisibility(View.GONE);
            findViewById(R.id.divider_3).setVisibility(View.GONE);
            findViewById(R.id.divider_5).setVisibility(View.GONE);
        } else {
            /*nameField.setVisibility(View.VISIBLE);
            descField.setVisibility(View.VISIBLE);
            catField.setVisibility(View.VISIBLE);
            locField.setVisibility(View.VISIBLE);*/
        }

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
            //locField.setText(String.format("" + lastLocation.getLatitude() + " , " + lastLocation.getLongitude()));
            //Toast.makeText(this, String.format("" + lastLocation.getLatitude() + "," + lastLocation.getLongitude()), Toast.LENGTH_LONG).show();
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
        if(current_type == -1) {
            Toast.makeText(this, "Please choose media.", Toast.LENGTH_SHORT).show();
            return;
        }
        r = new Random();
        rand = r.nextInt(5);

        if(!isAttempt) {
            if (locField.getText().toString().trim().length() <= 0 ||
                    nameField.getText().toString().trim().length() <= 0 ||
                    descField.getText().toString().trim().length() <= 0 ||
                    numField.getText().toString().trim().length() <= 0) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            OneUpWebRequest oneUpWebRequest = new ChallengePostWebRequest(getChallenge(), new RequestHandler<String>() {
                @Override
                public void onSuccess(String response) {
                    if (response != null) {
                        Intent result = new Intent();
                        result.putExtra(EXTRA_ID, response);
                        setResult(Activity.RESULT_OK, result);
                        finish();

                        responseID = response;

                        Log.d(TAG, "In challenge. response " + response);

                        byte[] inputData = new byte[0];

                        try {
                            InputStream iStream = getContentResolver().openInputStream(uriSavedImage);
                            inputData = getBytes(iStream);
                        } catch (Exception e) {
                            Log.d("HEY", "issue converting uri to bytes");
                        }

                        OneUpWebRequest oneUpWebRequest1 = new AttemptPostWebRequest(getAttempt(), inputData, new RequestHandler<String>() {
                            @Override
                            public void onSuccess(String response) {
                                Log.d(TAG, "In Attempt. ResponseID = " + responseID);
                                Log.d(TAG, "Attempt response = " + response);
                                Toast.makeText(ChallengeCreationActivity.this, "Uploaded challenge!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "Failed to post attempt");
                            }
                        });

                        Log.d(TAG, "After attempt post attempt");


                    }
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "Failed to post challenge");
                }
            });
        } else {
            if (numField.getText().toString().trim().length() <= 0) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            responseID = ChallengeDetailActivity.mChallenge.id;

            byte[] inputData = new byte[0];

            try {
                InputStream iStream = getContentResolver().openInputStream(uriSavedImage);
                inputData = getBytes(iStream);
            } catch (Exception e) {
                Log.d(TAG, "issue converting uri to bytes");
            }

            OneUpWebRequest oneUpWebRequest1 = new AttemptPostWebRequest(getAttempt(), inputData, new RequestHandler<String>() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "In Attempt. ResponseID = " + responseID);
                    Log.d(TAG, "Attempt response = " + response);
                    Toast.makeText(ChallengeCreationActivity.this, "Uploaded attempt!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();

                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "Failed to post attempt");
                }
            });

            Log.d(TAG, "Trying attempt");

        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private Challenge getChallenge() {
        Challenge c = new Challenge();

        c.name = nameField.getText().toString();
        //c.owner = NewUserActivity.username;
        c.owner = "Arthur Dent";

        c.desc = descField.getText().toString();
        c.categories = catField.getText().toString().split(",");
        //c.location = locField.getText().toString();
        c.location = locID;
        c.pattern = String.format("Pattern Number " + (r.nextInt(1) + 1000));
        return c;
    }

    private Attempt getAttempt() {

        Attempt c = new Attempt();

        c.challenge_id = responseID;
        c.votes_num = 0;
        c.likes_num = 0;
        c.owner = "Arthur Dent";
        c.desc = numField.getText().toString();
        
        return c;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageButton imgButton = (ImageButton) (findViewById(R.id.challenge_media_button));
        imgButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgButton.setVisibility(View.VISIBLE);

        VideoView videoView = new VideoView(this);//(VideoView) (findViewById(R.id.view_video));
        videoView.setVisibility(View.INVISIBLE);

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
                current_type = TYPE_PICTURE;

            } else {
                Log.e(TAG, "Something happened during Image capture.");
            }

        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            imgButton.setScaleType(ImageView.ScaleType.CENTER);
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                imgButton.setScaleType(ImageView.ScaleType.CENTER);
                imgButton.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Video saved to:\n" +
                        uriSavedImage.toString(), Toast.LENGTH_LONG).show();
                Log.v(TAG, String.format("Video saved to:\n" + uriSavedImage.toString()));

                current_type = TYPE_VIDEO;
                videoView.setVideoURI(uriSavedImage);
                videoView.requestFocus();
                videoView.start();

            } else {
                Log.e(TAG, "Something happened during Video capture.");
            }

        } else if(requestCode == SELECT_PHOTO_ACTIVITY_REQUEST_CODE) {
            if(resultCode == RESULT_OK){

                uriSavedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        uriSavedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                challenge_pic = BitmapFactory.decodeFile(filePath);
                imgButton.setImageBitmap(challenge_pic);

                current_type = TYPE_PICTURE;
                Toast.makeText(ChallengeCreationActivity.this, "File Path = " + filePath, Toast.LENGTH_LONG).show();
                Log.v(TAG, String.format(TAG, "Image selected from " + filePath));
            }
        } else if (requestCode == SELECT_VIDEO_ACTIVITY_REQUEST_CODE) {
            imgButton.setScaleType(ImageView.ScaleType.CENTER);
            if(resultCode == RESULT_OK){
                imgButton.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);

                uriSavedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        uriSavedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                Toast.makeText(ChallengeCreationActivity.this, "File Path = " + filePath, Toast.LENGTH_LONG).show();
                Log.v(TAG, String.format(TAG, "Video selected from " + filePath));

                current_type = TYPE_VIDEO;
                videoView.setVideoURI(uriSavedImage);
                videoView.requestFocus();
                videoView.start();
            }
        }
        if(resultCode != RESULT_OK) {
            current_type = -1;
        }
        Log.d(TAG, String.format("Current Type is " + current_type));
    }

    //listener for location
    public void selectLocation(View v) {
        try {
            final ArrayList<com.purduecs.kiwi.oneup.models.Location> locations = new ArrayList<>();

            OneUpWebRequest oneUpWebRequest = new LocationGetWebRequest(String.format("" + lastLocation.getLatitude())
                    , String.format("" + lastLocation.getLongitude()), new RequestHandler<ArrayList<ArrayList<String>>>() {
                @Override
                public void onSuccess(ArrayList<ArrayList<String>> response) {
                    int i = 0;
                    com.purduecs.kiwi.oneup.models.Location location;
                    for (ArrayList<String> a : response) {
                        location = new com.purduecs.kiwi.oneup.models.Location(a.get(0), a.get(1));
                        locations.add(location);
                    }

                    //sort the locations
                    Collections.sort(locations, new Comparator<com.purduecs.kiwi.oneup.models.Location>() {
                        @Override
                        public int compare(com.purduecs.kiwi.oneup.models.Location lhs, com.purduecs.kiwi.oneup.models.Location rhs) {
                            return lhs.name.compareToIgnoreCase(rhs.name);
                        }
                    });

                    AlertDialog.Builder media_sel = new AlertDialog.Builder(ChallengeCreationActivity.this);
                    media_sel.setTitle("Select Location");
                    final CharSequence[] items2 = new CharSequence[locations.size()];
                    i = 0;

                    for (com.purduecs.kiwi.oneup.models.Location s : locations) {
                        items2[i] = s.name;
                        i++;
                    }

                    media_sel.setItems(items2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            locField.setText(locations.get(item).name);
                            locID = locations.get(item).id;
                            dialog.dismiss();
                        }
                    });
                    media_sel.show();
                    Log.d(TAG, "Getting locations successful!");
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "Something went wrong in populating our location view");
                }
            });


        } catch (Exception e) {
            Log.e(TAG, "Exception in setting location. " + e.getMessage());
            locField.setText(String.format("" + lastLocation.getLatitude() + " , " + lastLocation.getLongitude()));
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

    public void clickNumberField(View v) {

        LayoutInflater inflater = getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String num;
                        switch (dialogType) {
                            case 0:
                                num = Integer.toString(
                                        ((NumberPicker) (
                                                (AlertDialog) dialog).findViewById(R.id.number_picker))
                                                .getValue());
                                break;
                            case 1:
                                num = (String)((Spinner)(
                                        (AlertDialog) dialog).findViewById(R.id.text_spinner))
                                        .getSelectedItem();
                                break;
                            default:
                                num = "0";
                                break;
                        }
                        nextNumField(num);
                    }
                });


        builder.setCustomTitle(inflateTitle(builder.getContext(), "24", "Pushups", 0));

        final FrameLayout frameView = new FrameLayout(this);
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();

        dialogType = 0;
        dialoglayout = inflater.inflate(R.layout.pick_attempt_number_dialog, frameView);

        Spinner type = (Spinner)dialoglayout.findViewById(R.id.type_dropdown);
        type.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"Numeric", "Text"}));
        type.setOnItemSelectedListener(spinnerListener);

        NumberPicker picker = (NumberPicker)dialoglayout.findViewById(R.id.number_picker);
        picker.setMinValue(0);
        picker.setMaxValue(10000);
        picker.setValue(24);

        Spinner text = (Spinner)dialoglayout.findViewById(R.id.text_spinner);
        text.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"Most", "Least"}));

        alertDialog.show();
    }

    private void nextNumField(final String num) {

        LayoutInflater inflater = getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String qual = ((TextView) ((AlertDialog) dialog).findViewById(R.id.qualifier))
                                .getText().toString();

                        numField.setText(num + " " + qual);

                        dialoglayout = null;
                    }
                });


        builder.setCustomTitle(inflateTitle(builder.getContext(), num, "Pushups", 1));

        final FrameLayout frameView = new FrameLayout(this);
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();

        dialoglayout = inflater.inflate(R.layout.pick_attempt_qual_dialog, frameView);
        alertDialog.show();
    }

    private View inflateTitle(Context c, String one, String two, int focus) {
        View title = View.inflate(c, R.layout.pick_attempt_dialog_title, null);

        ((TextView)title.findViewById(R.id.title_num)).setText(one);
        ((TextView)title.findViewById(R.id.title_qual)).setText(two);
        if (focus == 0)
            ((TextView)title.findViewById(R.id.title_qual))
                    .setTextColor(getResources().getColor(R.color.transparentWhite));
        else
            ((TextView)title.findViewById(R.id.title_num))
                    .setTextColor(getResources().getColor(R.color.transparentWhite));

        return title;
    }

    private View dialoglayout;
    private int dialogType;

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String type = (String)parent.getItemAtPosition(position);
            switch (type) {
                case "Numeric":
                    dialoglayout.findViewById(R.id.number_picker).setVisibility(View.VISIBLE);
                    dialoglayout.findViewById(R.id.text_spinner).setVisibility(View.GONE);
                    dialogType = 0;
                    break;
                case "Text":
                    dialoglayout.findViewById(R.id.number_picker).setVisibility(View.GONE);
                    dialoglayout.findViewById(R.id.text_spinner).setVisibility(View.VISIBLE);
                    dialogType = 1;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}


