package org.libsdl.openxcom;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.libsdl.openxcom.config.Config;
import org.libsdl.openxcom.util.FilesystemHelper;

public class PreloaderActivity extends Activity {

	private static final String TAG = "PreloaderActivity";
	
	protected AssetManager assets = null;
	protected TextView preloaderLog = null;
	protected Context context;
	private ProgressDialog pd;
	
	private Config config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        config = Config.createInstance(this);
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preloader);
		context = this;
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
        new AsyncTask<Void, String, Void>()
        {
            Set<String> assetContents;

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setTitle("Pre-loading OpenXcom 1.0...");
                pd.setMessage("Initializing...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                Log.i(TAG, "Updating game data...");
                assetContents = new TreeSet<>();
                pd.show();
            }

            public void onProgressUpdate(String... message) {
                /*if (!pd.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.show();
                        }});
                }*/
                pd.setMessage(message[0]);
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                // Only copy old files if necessary
                if (config.hasOldFiles() && !hasGameFiles()) {
                    publishProgress("Copying old game files to new location...");
                    String[] gameFolders = new String[] {"GEODATA",
                            "GEOGRAPH",
                            "MAPS",
                            "ROUTES",
                            "SOUND",
                            "TERRAIN",
                            "UFOGRAPH",
                            "UFOINTRO",
                            "UNITS"};
                    String origPath = config.getOldFilesPath();
                    try {
                        for(String dir : gameFolders) {
                            Log.i(TAG, "Copying " + dir + "...");
                            FilesystemHelper.copyFolder(new File(origPath + "/" + dir),
                                    new File(config.getDataFolderPath() + "/" + dir),
                                    true);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error copying directory!");
                        Log.e(TAG, e.getMessage());
                    }
                }

                // Get a list of baked-in assets
                publishProgress("Generating a list of assets...");
                try {
                    for (String assetName : assets.list("")) {
                        assetContents.add(assetName);
                        Log.i(TAG, "Adding asset: " + assetName);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error while listing assets!");
                    Log.e(TAG, e.getMessage());
                }
                for (String archiveName : assetContents) {
                    if (archiveName.endsWith(".zip")) {
                        String checksumName = archiveName + ".MD5";
                        String version;
                        String currentVersion = config.getAssetVersion(archiveName);
                        if (assetContents.contains(checksumName)) {
                            byte buffer[] = new byte[1024];
                            try {
                                int length = assets.open(checksumName).read(buffer);
                                version = new String(buffer, 0, length - 2);
                            } catch (IOException e) {
                                Log.e(TAG, "Error while reading file archive checksum!");
                                Log.e(TAG, e.getMessage());
                                version = "undefined";
                            }
                        } else {
                            version = "undefined";
                        }
                        if (!version.equals(currentVersion)) {
                            publishProgress("Extracting " + archiveName);
                            Log.i(TAG, "Extracting " + archiveName + " to " + config.getDataFolderPath());
                            try {
                                FilesystemHelper.zipExtract(assets.open(archiveName), new File(config.getDataFolderPath()));
                                config.setAssetVersion(archiveName, version);
                            } catch (IOException e) {
                                Log.e(TAG, "Error while extracting " + archiveName);
                                Log.e(TAG, e.getMessage());
                            }
                        } else {
                            Log.i(TAG, "Skipping " + archiveName + " because the version is the same");
                        }
                    }
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
	 * Method that (crudely) checks if we have the game files.
	 * @return True if the game files are properly installed.
	 */
	protected boolean hasGameFiles() {
        try {
            ZipInputStream ufo1zip = new ZipInputStream(assets.open("3_UFO.zip"));
            ZipEntry ze = ufo1zip.getNextEntry();
            while (ze != null) {
                if(ze.getName().equals("UFO/TERRAIN/UFO1.PCK")) {
                    return true;
                }
                ze = ufo1zip.getNextEntry();
            }
        } catch (IOException e) {
            Log.w(TAG, "Could not open 3_UFO.zip; did you package your files correctly?");
        }
        return new File(config.getDataFolderPath() + "/UFO/TERRAIN/UFO1.PCK").exists();
	}
}
