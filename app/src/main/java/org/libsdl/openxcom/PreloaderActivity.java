package org.libsdl.openxcom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
import android.widget.TextView;

import org.libsdl.openxcom.FilesystemHelper;

public class PreloaderActivity extends Activity {

	private static final String TAG = "PreloaderActivity";
	
	SharedPreferences prefs;
	
	protected AssetManager assets = null;
	protected TextView preloaderLog = null;
	protected Context context;
	private ProgressDialog pd;
	
	String gamePath;
	
	final String dataMarkerName = "openxcom_data_marker";
	final String translationMarkerName = "openxcom_translation_marker";
	final String patchMarkerName = "openxcom_patch_marker";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preloader);
		context = this;
		prefs = getSharedPreferences(DirsConfigActivity.PREFS_NAME, 0);
		gamePath = prefs.getString(DirsConfigActivity.DATA_PATH_KEY, "");
		if (gamePath.equals("")) {
			// Looks like we're running for the first time.
			// We'll just make our best attempt at guessing where the game is.
			// TODO: A better approach would be to launch a DirsConfigActivity
			// to allow the user to select the appropriate directories on his own.
			final String defaultGamePath = Environment.getExternalStorageDirectory().getPath() + "/OpenXcom";
			gamePath = defaultGamePath + "/data";
			// We also should put some data into the preferences!
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putString(DirsConfigActivity.DATA_PATH_KEY, gamePath);
			prefsEditor.putString(DirsConfigActivity.SAVE_PATH_KEY, defaultGamePath);
			prefsEditor.putString(DirsConfigActivity.CONF_PATH_KEY, defaultGamePath);
			prefsEditor.commit();
		}
		preloaderLog = (TextView) findViewById(R.id.preloaderLog);
		assets = getAssets();
	}
	
	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}
	
	/**
	 * We have to determine if the user has the latest version
	 * of the OpenXcom data, languages and patches. If not, we'll have to install them.
	 */
	
	@Override
	protected void onStart() {
		super.onStart();
		// We only want to do all this if we have the game files.
		if (hasGameFiles()) {
			// Check what needs updating
			final boolean dataNeedsUpdating = needsUpdating(dataMarkerName);
			final boolean translationNeedsUpdating = needsUpdating(translationMarkerName);
			final boolean needsPatch = needsUpdating(patchMarkerName);
			if (dataNeedsUpdating || translationNeedsUpdating || needsPatch) {
				new AsyncTask<Void, String, Void>() 
				{
			
					@Override
					protected void onPreExecute() {
						pd = new ProgressDialog(context);
						pd.setTitle("Pre-loading OpenXcom 1.0...");
						pd.setMessage("Initializing...");
						pd.setCancelable(false);
						pd.setIndeterminate(true);
						pd.show();
						Log.i(TAG, "Updating game data...");
					}
					
					public void onProgressUpdate(String... message) {
						pd.setMessage(message[0]);
					}
					
					@Override
					protected Void doInBackground(Void... arg0) {
						try {
							publishProgress("Checking data version...");
							if (dataNeedsUpdating) {
								publishProgress("Extracting data...");
								Log.i(TAG, "Updating data files");
								extractFile("data.zip", gamePath + "/");
								copyMarker(dataMarkerName);
							}
							publishProgress("Checking translations version...");
							if (translationNeedsUpdating) {
								publishProgress("Extracting translations...");
								Log.i(TAG, "Updating translations");
								extractFile("latest.zip", gamePath + "/Language/");
								copyMarker(translationMarkerName);
							}
							publishProgress("Checking patch version...");
							if (needsPatch) {
								publishProgress("Applying patch...");
								Log.i(TAG, "Patching game files");
								extractFile("universal-patch.zip", gamePath + "/");
								copyMarker(patchMarkerName);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						if (pd != null) {
							pd.dismiss();
						}
						Log.i(TAG, "Preloading finished.");
						passExecution();	
					}
					
				}.execute((Void[]) null);	
			}
			else
			{
				passExecution();
			}
		}
		else
		{
			// We don't need to do anything, just pass execution further
			passExecution();
		}
	}
	
	/**
	 * This method gets called by AsyncThread to execute the main activity.
	 * Since this activity can actually be started by another activity,
	 * we should also check the previous intent.
	 */
	
	protected void passExecution() {
		Intent calledIntent = this.getIntent();
		Bundle extParams = calledIntent.getExtras();
		String calledFrom = null;
		if (extParams != null) {
			calledFrom = extParams.getString("calledFrom");
		}
		if (calledFrom != null)
		{
			Log.i(TAG, "Got extra parameters!");
			Log.i(TAG, "calledFrom: " + calledFrom);
			if (calledFrom.equals("DirsConfigActivity")) {
				// We were started from the file chooser dialog activity,
				// so we should return to it.
				Log.i(TAG, "Called from DirsConfigActivity, returning");
				setResult(0);
				finish();
			}
		} else {
			Log.i(TAG, "Launching OpenXcom activity");
			Intent intent = new Intent(this, OpenXcom.class);
			startActivity(intent);
		}
	}
	
	
	
	/**
	 * A helper method to extract the contents of assetName to extractPath.
	 * Be extremely careful to put things where they're supposed to be!
	 */
	
	protected void extractFile(String assetName, String extractPath) {
		InputStream is;
		try {
			is = assets.open(assetName);
			FilesystemHelper.zipExtract(is, new File(extractPath));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that (crudely) checks if we have the game files.
	 * @return True if the game files are properly installed.
	 */
	
	protected boolean hasGameFiles() {
		File checkFile = new File(gamePath + "/GEODATA/PALETTES.DAT");
		Log.i(TAG, "Checking for game data at " + gamePath);
		if (checkFile.exists()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Method that checks we need to update the data.
	 * @return True if we need to update data files.
	 */
	
	protected boolean needsUpdating(String markerName) {
		final String markerPath = gamePath;
		File checkFile = new File(markerPath + "/" + markerName);
		// It's our first time here, by the looks of it.
		if (!checkFile.exists()) {
			return true;
		}
		// Check file contents against the one we have in out assets.
		try {
			InputStream assetFileIS = assets.open(markerName);
			InputStream checkFileIS = new FileInputStream (checkFile);
			boolean areSame = FilesystemHelper.sameContents(assetFileIS, checkFileIS);
			assetFileIS.close();
			checkFileIS.close();
			if (areSame) {
				return false;
			}
			return true;
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return true;
		}
		
	}
		
	/**
	 * Copies the marker from assets to data folder.
	 * @param markerName Filename of the data marker.
	 */
	
	protected void copyMarker(String markerName) {
		final String markerPath = gamePath;
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	    	in = assets.open(markerName);
	        File outFile = new File(markerPath + "/" + markerName);
	        out = new FileOutputStream(outFile);
	        FilesystemHelper.copyStream(in, out);
	        in.close();
	        in = null;
	        out.flush();
	        out.close();
	        out = null;
	    } 
	    catch(IOException e) {
	            Log.e(TAG, "Failed to copy asset file: " + markerName, e);
	        }       
	    }

	
	
}
