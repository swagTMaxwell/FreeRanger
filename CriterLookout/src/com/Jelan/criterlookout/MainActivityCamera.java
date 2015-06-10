package com.Jelan.criterlookout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivityCamera extends Activity {
	protected static final String TAG = null;
	boolean bDevHasCamera;
	private Camera mCamera;
	private CameraPreview mPreview;
	private Button captureButton;
	private Button doneCapturing;
	// this links to the done button and will be clicked by user once they are
	// satisfied with pictures
	boolean keepCapturing = true;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private ShutterCallback shutter;
	private PictureCallback raw;
	private PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

		}

		private File getOutputMediaFile(int type) {
			Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			return null;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initiallizeLayout();

	}

	public void initiallizeLayout() {
		setContentView(R.layout.camerapreview);

		if (mCamera == null) {
			mCamera = getCameraInstance(this);
		}

		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.FLCameraPreview);
		preview.addView(mPreview);
		captureButton = (Button) findViewById(R.id.capture);
		doneCapturing = (Button) findViewById(R.id.DoneCapturing);
		
		SetListeners(captureButton, doneCapturing);
	}

	// get instance of camera
	private static Camera getCameraInstance(Context context) {
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			Toast toast = new Toast(context);
			toast.makeText(context, "Could not load Camera", toast.LENGTH_SHORT)
					.show();
		}
		return camera;

	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_camera, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onPause() {
		super.onPause();

		mCamera = null;
	}

	public void onResume() {
		super.onResume();

		initiallizeLayout();
	}

	private void SetListeners(Button CaptureButton, Button DoneCapturingButton) {
		captureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCamera.takePicture(null, null, jpeg);
				if (keepCapturing == false) {
					mCamera.stopPreview();
					mCamera.release();
				}

			}
		});
		doneCapturing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				keepCapturing = false;

			}
		});
	}
}
