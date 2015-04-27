package org.libsdl.openxcom;

import java.io.File;
import java.io.IOException;

import org.libsdl.openxcom.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;



public class DirsConfigActivity extends Activity {
	
	SharedPreferences preferences; 
	
	// This can be accessed from outside the activity, so I won't need to remember the exact string.
	public static final String PREFS_NAME = "OpenXcomPrefs";
	
	// These values will be stored in the preferences file.
	private boolean useAppCache;
	private boolean useAppCacheSave;
	private boolean useAppCacheConf;
	protected String dataPath;
	protected String savePath;
	protected String confPath;
	
	// These keys will be accesible outside the activity.
	public static final String USE_APP_CACHE_KEY = "useAppCache";
	public static final String USE_APP_CACHE_SAVE_KEY = "useAppCacheSave";
	public static final String USE_APP_CACHE_CONF_KEY = "useAppCacheConf";
	public static final String DATA_PATH_KEY = "dataPath";
	public static final String SAVE_PATH_KEY = "savePath";
	public static final String CONF_PATH_KEY = "confPath";
	
	private CheckBox useAppCacheCheck;
	private CheckBox useAppCacheSaveCheck;
	private CheckBox useAppCacheConfCheck;
	
	private EditText dataPathText;
	private EditText savePathText;
	private EditText confPathText;
	
	// We'll need these to be able to disable them at will.
	private Button dataBrowseButton;
	private Button saveBrowseButton;
	private Button confBrowseButton;
	
	// Stored values for directories:
	//  external: outside private storage
	//  private: in private storage
	private String dataPath_external;
	private String savePath_external;
	private String confPath_external;
	private String dataPath_private;
	private String savePath_private;
	private String confPath_private;
	
	private AlertDialog copyWarningDialog;
	private FileChooserDialog dataDialog;
	private FileChooserDialog saveDialog;
	private FileChooserDialog confDialog;
	
	public Context context;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT >= 14) {
			setTheme(android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar); // Theme_DeviceDefault_Light_DialogWhenLarge_NoActionBar
		}
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_dirs_config);
		
		// Initialize default paths
		// TODO: Save these paths as well, so that preferences would be nicer to use.
		dataPath_external = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenXcom/data";
		savePath_external = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenXcom";
		confPath_external = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenXcom";

		dataPath_private = getExternalFilesDir(null).getAbsolutePath() + "/data";
		savePath_private = getExternalFilesDir(null).getAbsolutePath(); // Don't use another subfolder; it won't be created automagically.
		confPath_private = getExternalFilesDir(null).getAbsolutePath() + "/conf";

		dataBrowseButton = (Button) findViewById(R.id.dataBrowseButton);
		saveBrowseButton = (Button) findViewById(R.id.saveBrowseButton);
		confBrowseButton = (Button) findViewById(R.id.confBrowseButton);

		useAppCacheCheck = (CheckBox) findViewById(R.id.useDataCacheCheck);
		useAppCacheSaveCheck = (CheckBox) findViewById(R.id.useSaveCacheCheck);
		useAppCacheConfCheck = (CheckBox) findViewById(R.id.useConfCacheCheck);

		dataPathText = (EditText) findViewById(R.id.dataPathEdit);

		savePathText = (EditText) findViewById(R.id.savePathEdit);

		confPathText = (EditText) findViewById(R.id.confPathEdit);

		// Initialize preferences

		preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		populatePreferences();
		
		// Prepare dialogs for showing
		setupDialogs();

		// Set view elements according to current preferences
		useAppCacheCheck.setChecked(useAppCache);
		useAppCacheSaveCheck.setChecked(useAppCacheSave);
		useAppCacheConfCheck.setChecked(useAppCacheConf);
		
		dataPathText.setText(dataPath);
		dataPathText.setInputType(0);
		savePathText.setText(savePath);
		savePathText.setInputType(0);
		confPathText.setText(confPath);
		confPathText.setInputType(0);
		
		saveBrowseButton.setEnabled(!useAppCacheSave);
		confBrowseButton.setEnabled(!useAppCacheConf);

		useAppCacheCheck.setChecked(useAppCache);
		useAppCacheSaveCheck.setChecked(useAppCacheSave);
		useAppCacheConfCheck.setChecked(useAppCacheConf);


		// Set listeners for checkboxes

		useAppCacheCheck.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// Alert the user that this option will cause his data to be
						// copied to the new location, resulting in loss of several megabytes.
						if (isChecked) {
							copyWarningDialog.show();
						} else {
							useAppCache = false;
							dataPath = dataPath_external;
							dataPathText.setText(dataPath);
						}
					}
				} );

		useAppCacheSaveCheck.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						saveBrowseButton.setEnabled(!isChecked);
						if (isChecked) {
							savePath = savePath_private;
						} else {
							savePath = savePath_external;
						}
						savePathText.setText(savePath);
						useAppCacheSave = isChecked;
					}
				} );

		useAppCacheConfCheck.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						confBrowseButton.setEnabled(!isChecked);
						if (isChecked) {
							confPath = confPath_private;
						} else {
							confPath = confPath_external;
						}
						confPathText.setText(confPath);
						useAppCacheConf = isChecked;
					}
				} );
				
	}
	
	@Override
	protected void onStop() {
		savePreferences();
		dismissDialogs();
		super.onStop();
	}
	
	private void populatePreferences() {
		// Check if that's the first time we're running the app
		if (!preferences.contains(USE_APP_CACHE_KEY)) {
			useAppCache = false;
			useAppCacheSave = false;
			useAppCacheConf = false;
			dataPath = dataPath_external;
			savePath = savePath_external;
			confPath = confPath_external;
			//savePreferences();
		} else {
			// Load shared preferences
			Log.i("com.example.test_config", "Shared preferences present!");
			useAppCache = preferences.getBoolean(USE_APP_CACHE_KEY, false);
			useAppCacheSave = preferences.getBoolean(USE_APP_CACHE_SAVE_KEY, false);
			useAppCacheConf = preferences.getBoolean(USE_APP_CACHE_CONF_KEY, false);
			dataPath = preferences.getString(DATA_PATH_KEY, "");
			savePath = preferences.getString(SAVE_PATH_KEY, "");
			confPath = preferences.getString(CONF_PATH_KEY, "");
		}
	}
	
	private void savePreferences() {
		SharedPreferences.Editor editor = preferences.edit();
		// Save current config
		editor.putBoolean(USE_APP_CACHE_KEY, useAppCache);
		editor.putBoolean(USE_APP_CACHE_SAVE_KEY, useAppCacheSave);
		editor.putBoolean(USE_APP_CACHE_CONF_KEY, useAppCacheConf);
		editor.putString(DATA_PATH_KEY, dataPath);
		editor.putString(SAVE_PATH_KEY, savePath);
		editor.putString(CONF_PATH_KEY, confPath);
		
		editor.commit();
	}
	
	public void dataButtonPress(View view) {
		dataDialog.show();
	}
	
	public void saveButtonPress(View view) {
		saveDialog.loadFolder(savePath);
		saveDialog.show();
	}
	
	public void confButtonPress(View view) {
		confDialog.loadFolder(confPath);
		confDialog.show();
		
	}
	
	// This prepares our dialog windows to be shown.
	private void setupDialogs() {
		AlertDialog.Builder copyDlgBuilder = new AlertDialog.Builder(this);
		copyDlgBuilder.setTitle("Warning");
		copyDlgBuilder.setMessage("You'll need to specify the path to game's data.\n"+
								  "This data will be copied to the app's private storage.\n" +
								  "Do you wish to proceed?");
		copyDlgBuilder.setCancelable(true);
		copyDlgBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						useAppCache = true;
						useAppCacheCheck.setChecked(true);
						dataPath = dataPath_private;
						dataPathText.setText(dataPath);
					}
				});
		copyDlgBuilder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		copyDlgBuilder.setOnCancelListener(
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						useAppCache = false;
						useAppCacheCheck.setChecked(false);
						dataPath = dataPath_external;
						dataPathText.setText(dataPath);
					}
				});

		copyWarningDialog = copyDlgBuilder.create();
		
		dataDialog = new FileChooserDialog(this);
		dataDialog.loadFolder(dataPath_external);
		if (useAppCache) {
			// Ignore ZIP files for now - it'll be too confusing.
			// dataDialog.setFilter(".*zip|.*ZIP");
			dataDialog.setShowConfirmation(true, false);
			dataDialog.setFolderMode(true);
		}
		dataDialog.setFolderMode(true);
		dataDialog.setShowOnlySelectable(true);
		dataDialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
			
			@Override
			public void onFileSelected(Dialog source, File folder, String name) {
				// Do nothing, as we don't exactly care for creating new files.
			}
			
			@Override
			public void onFileSelected(Dialog source, File file) {
				source.hide();
				if (useAppCache) {
					if (file.isDirectory()) {
						copyData(file);
					} else {
						try {
							FilesystemHelper.zipExtract(file, new File(dataPath_private));
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					File dataCheck = new File(file.getAbsolutePath() + "/data");
					if (dataCheck.exists()) {
						dataPath = dataCheck.getAbsolutePath();
					} else {
						dataPath = file.getAbsolutePath();
					}
					dataPathText.setText(dataPath);
				}
			}
		});
		
		saveDialog = new FileChooserDialog(this);
		saveDialog.loadFolder(savePath);
		saveDialog.setFolderMode(true);
		saveDialog.setCanCreateFiles(true);
		saveDialog.setShowOnlySelectable(true);
		saveDialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
			
			@Override
			public void onFileSelected(Dialog source, File folder, String name) {
				File saveFolder = new File(folder.getAbsolutePath() + "/" + name);
				
				if (saveFolder.mkdir()) {
					Toast.makeText(source.getContext(), "Successfuly created folder: " + saveFolder.getName(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(source.getContext(), "Could not create folder: " + saveFolder.getName(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFileSelected(Dialog source, File file) {
				source.hide();
				savePath_external = file.getAbsolutePath();
				savePath = savePath_external;
				savePathText.setText(savePath);
			}
			
		});
		
		confDialog = new FileChooserDialog(this);
		confDialog.loadFolder(confPath);
		confDialog.setFolderMode(true);
		confDialog.setCanCreateFiles(true);
		confDialog.setShowOnlySelectable(true);
		confDialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
			
			@Override
			public void onFileSelected(Dialog source, File folder, String name) {
				File confFolder = new File(folder.getAbsolutePath() + "/" + name);
				
				if (confFolder.mkdir()) {
					Toast.makeText(source.getContext(), "Successfuly created folder: " + confFolder.getName(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(source.getContext(), "Could not create folder: " + confFolder.getName(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFileSelected(Dialog source, File file) {
				source.hide();
				confPath_external = file.getAbsolutePath();
				confPath = confPath_external;
				confPathText.setText(confPath);
			}
		});		
	}
	
	// This cleans up all dialogs.
	private void dismissDialogs() {
		copyWarningDialog.dismiss();
		dataDialog.dismiss();
		saveDialog.dismiss();
		confDialog.dismiss();
	}
	
	// This will start the preloader, which will update the directories.
	public void applyButtonPress(View view) {
		savePreferences();
		Intent intent = new Intent(this, PreloaderActivity.class);
		intent.putExtra("calledFrom", "DirsConfigActivity");
		Log.i("DirsConfigActivity", "Launching Preloader to patch our files");
		startActivityForResult(intent, 0);
	}
	
	
	// Pass execution to SDLActivity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("DirsConfigActivity", "onActivityResult: got back from Preloader, passing execution to OpenXcom");
		setResult(0, new Intent());
		finish();
	}
	
	// Wrap the copy process in an AsyncTask while showing a ProgressDialog.
	protected void copyData(File in_dir)
	{
		new AsyncTask<File, String, Void>() {
			
			ProgressDialog pd;
			
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Copying X-Com data...");
				pd.setMessage("Initializing...");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
				Log.i("OpenXcom", "AsyncTask started");
			}
			
			public void onProgressUpdate(String... message) {
				pd.setMessage(message[0]);
			}
			
			@Override
			protected Void doInBackground(File... arg0) {
				try {
					publishProgress("Copying files...");
					Log.i("DirsAsyncTask", "Calling copyFolder...");
					FilesystemHelper.copyFolder(arg0[0], new File(dataPath_private), true);
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
				Log.i("DirsConfigActivity", "Finishing asynctask...");	
			}
			
		}.execute(in_dir);
		
	}
}
