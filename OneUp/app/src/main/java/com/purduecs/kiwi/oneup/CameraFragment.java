package com.purduecs.kiwi.oneup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.purduecs.kiwi.oneup.views.AutoFitTextureView;
import com.purduecs.kiwi.oneup.views.CameraSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class CameraFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback {

    private static final String EXTRA_FILE_NAME = "com.purduecs.kiwi.oneup.camerafragmentfilecrap";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private File mFile;

    private Camera mCamera;

    private Thread mSavePictureThread;

    private CameraSurfaceView mSurfaceView;


    private static final int STATE_PREVIEW = 0;
    private static final int STATE_TAKEN = 1;
    private int mPictureState;

    public static CameraFragment newInstance(String file) {
        CameraFragment c = new CameraFragment();
        Bundle b = new Bundle();
        b.putString(EXTRA_FILE_NAME, file);
        c.setArguments(b);
        return c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
        mSurfaceView = new CameraSurfaceView(v.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSurfaceView.setLayoutParams(params);
        v.addView(mSurfaceView, 0);
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return v;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.picture).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        view.findViewById(R.id.stopwatch).setOnClickListener(this);
        //mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFile = new File(getArguments().getString(EXTRA_FILE_NAME));
        Log.d("HEY", mFile.toString());
        //mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
        //Log.d("HEY", mFile.toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (mSurfaceView.isActivated() && mCamera == null) {
            openCamera();
        }*/
    }

    @Override
    public void onPause() {
        closeCamera();
        if (mSavePictureThread != null) { /* TODO: maybe interrupt it */ }
        super.onPause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera()) {
            Log.d("HEY", "failed to open for some reason");
            closeCamera();
            ErrorDialog.newInstance("Error opening camera").show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
    }

    private boolean openCamera() {
        Log.d("HEY", "opening");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return false;
        }
        boolean qOpened = false;
        Log.d("HEY", "opening2");
        try {
            closeCamera();
            mCamera = Camera.open();
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e("HEY", "failed to open Camera");
            e.printStackTrace();
        }

        Log.d("HEY", "opening3");

        if (qOpened) {
            Log.d("HEY", "opening4");
            //List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            //mSupportedPreviewSizes = localSizes;
            mSurfaceView.requestLayout();

            /*try {
                mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            mCamera.setPreviewCallback(mPreviewCallback);

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();

            mPictureState = STATE_PREVIEW;
        }

        return qOpened;
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (mCamera != null) {
                mSurfaceView.render(data, camera.getParameters().getPreviewSize());
            }
        }
    };

    public void closeCamera() {
        Log.d("HEY", "closing camera");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            Log.d("HEY", "closing camera");
        }
    }

    private void requestCameraPermission() {
        if (FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance("Request permission?")
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picture:
                if (mPictureState == STATE_PREVIEW) {
                    mCamera.takePicture(null, null, jpegCallback);
                    mPictureState = STATE_TAKEN;
                }
                break;
            case R.id.cancel:
                if (mPictureState == STATE_TAKEN) {
                    mCamera.startPreview();
                    mPictureState = STATE_PREVIEW;
                }
                break;
            case R.id.stopwatch:
                if (mPictureState == STATE_PREVIEW) {
                    mSurfaceView.toggleTimer();
                }
                break;
        }
    }

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //mSavePictureThread = new Thread(new ImageSaver(data, mFile));
            mSavePictureThread = new Thread(new ImageSaver(mSurfaceView.getPicture(camera.getParameters().getPreviewSize()), mFile, new FinishSaveListener() {
                @Override
                public void onFinish(boolean success) {
                    if (success) {
                        CameraFragment.this.getActivity().setResult(Activity.RESULT_OK);
                        CameraFragment.this.getActivity().finish();
                    }
                }
            }));
            mSavePictureThread.start();
        }
    };

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final byte[] mImage;

        private FinishSaveListener mListener;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(byte[] image, File file, FinishSaveListener listener) {
            mImage = image;
            mFile = file;
            mListener = listener;
        }

        @Override
        public void run() {
            byte[] bytes = mImage;
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mListener.onFinish(true);
        }

    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.width * lhs.height -
                    (long) rhs.width * rhs.height);
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);

            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        }
    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage("request permission?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    private abstract class FinishSaveListener {
        public abstract void onFinish(boolean success);
    }
}
