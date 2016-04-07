package com.purduecs.kiwi.oneup;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class NewUserProfileActivity extends AppCompatActivity {

    boolean chosen_image = false;
    private String TAG = "OneUP";

    private int SELECT_PHOTO_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_profile);
    }

    public void goNext(View v) {
        if (!chosen_image) {
            Toast.makeText(this, "Please choose an image.", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d(TAG, "Got image.");
            Intent intent = new Intent(this, NewsfeedActivity.class);
            startActivity(intent);
        }
    }

    public void goSkip(View v) {
        Log.d(TAG, "Skipping choosing image");
        Intent intent = new Intent(this, NewsfeedActivity.class);
        startActivity(intent);
    }

    public void selectMedia(View v) {
        Log.d(TAG, "Selecting Media...");

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SELECT_PHOTO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap profile_pic = BitmapFactory.decodeFile(filePath);
                ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
                imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageButton.setImageBitmap(profile_pic);

                Toast.makeText(this, "File Path = " + filePath, Toast.LENGTH_LONG).show();
                Log.v(TAG, String.format(TAG, "Profile Picture selected from " + filePath));

                chosen_image = true;
            }
        }
    }
}
