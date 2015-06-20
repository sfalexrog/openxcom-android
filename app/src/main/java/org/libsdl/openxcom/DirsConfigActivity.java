package org.libsdl.openxcom;

import java.io.File;
import java.io.IOException;

import org.libsdl.openxcom.config.Config;
import org.libsdl.openxcom.config.DataCheckResult;
import org.libsdl.openxcom.config.DataChecker;
import org.libsdl.openxcom.config.Xcom1DataChecker;
import org.libsdl.openxcom.util.FilesystemHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;



public class DirsConfigActivity extends Activity {
	
	private Config config;

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
    private Button installButton;
	
	private AlertDialog copyWarningDialog;
	private FileChooserDialog dataDialog;
	private FileChooserDialog saveDialog;
	private FileChooserDialog confDialog;
	private FileChooserDialog installDialog;

    private DataChecker checker;
	
	public Context context;

	private TextView xcom1Status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT >= 14) {
			setTheme(android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar); // Theme_DeviceDefault_Light_DialogWhenLarge_NoActionBar
		}
		super.onCreate(savedInstanceState);
		context = this;
        config = Config.getInstance();
        if (config == null) {
            config = Config.createInstance(context);
        }
		setContentView(R.layout.activity_dirs_config);

		dataBrowseButton = (Button) findViewById(R.id.dataBrowseButton);
		saveBrowseButton = (Button) findViewById(R.id.saveBrowseButton);
		confBrowseButton = (Button) findViewById(R.id.confBrowseButton);
        installButton = (Button) findViewById(R.id.dirsInstallFromDirButton);

		useAppCacheCheck = (CheckBox) findViewById(R.id.useDataCacheCheck);
		useAppCacheSaveCheck = (CheckBox) findViewById(R.id.useSaveCacheCheck);
		useAppCacheConfCheck = (CheckBox) findViewById(R.id.useConfCacheCheck);

		dataPathText = (EditText) findViewById(R.id.dataPathEdit);

		savePathText = (EditText) findViewById(R.id.savePathEdit);

		confPathText = (EditText) findViewById(R.id.confPathEdit);

        xcom1Status = (TextView) findViewById(R.id.dirsUfo1Status);

		// Prepare dialogs for showing
		setupDialogs();

        updateStatus();

		// Set view elements according to current preferences
		useAppCacheCheck.setChecked(config.getUseDataCache());
		useAppCacheSaveCheck.setChecked(config.getUseSaveCache());
		useAppCacheConfCheck.setChecked(config.getUseConfCache());
		
		dataPathText.setText(config.getDataFolderPath());
		dataPathText.setInputType(0);
		savePathText.setText(config.getSaveFolderPath());
		savePathText.setInputType(0);
		confPathText.setText(config.getConfFolderPath());
		confPathText.setInputType(0);
		
		saveBrowseButton.setEnabled(!config.getUseSaveCache());
		confBrowseButton.setEnabled(!config.getUseConfCache());

		useAppCacheCheck.setChecked(config.getUseDataCache());
		useAppCacheSaveCheck.setChecked(config.getUseSaveCache());
		useAppCacheConfCheck.setChecked(config.getUseConfCache());

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
                            config.setUseDataCache(false);
							dataPathText.setText(config.getDataFolderPath());
						}
					}
				} );

		useAppCacheSaveCheck.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						saveBrowseButton.setEnabled(!isChecked);
						if (isChecked) {
                            savePathText.setText(config.getExternalFilesDir().getAbsolutePath());
						} else {
                            savePathText.setText(config.getSaveFolderPath());
						}
						config.setUseSaveCache(isChecked);
					}
				} );

		useAppCacheConfCheck.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						confBrowseButton.setEnabled(!isChecked);
						if (isChecked) {
							confPathText.setText(config.getExternalFilesDir().getAbsolutePath());
						} else {
							confPathText.setText(config.getConfFolderPath());
						}
						config.setUseConfCache(isChecked);
					}
				} );
				
	}
	
	@Override
	protected void onStop() {
		config.save();
		dismissDialogs();
		super.onStop();
	}
	
	public void dataButtonPress(View view) {
		dataDialog.show();
	}
	
	public void saveButtonPress(View view) {
		saveDialog.loadFolder(config.getSaveFolderPath());
		saveDialog.show();
	}
	
	public void confButtonPress(View view) {
		confDialog.loadFolder(config.getConfFolderPath());
		confDialog.show();
		
	}

    public void installButtonPress(View view) {
        installDialog.show();
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
                        config.setUseDataCache(true);
						useAppCacheCheck.setChecked(true);
						dataPathText.setText(config.getExternalFilesDir().getAbsolutePath());
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
						config.setUseDataCache(false);
						useAppCacheCheck.setChecked(false);
						dataPathText.setText(config.getDataFolderPath());
					}
				});

		copyWarningDialog = copyDlgBuilder.create();
		
		dataDialog = new FileChooserDialog(this);
		dataDialog.loadFolder(config.getDataFolderPath());
		if (config.getUseDataCache()) {
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
				if (config.getUseDataCache()) {
					if (file.isDirectory()) {
						copyData(file, config.getExternalFilesDir());
					} else {
						try {
							FilesystemHelper.zipExtract(file, config.getExternalFilesDir());
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
                    config.setDataFolderPath(file.getAbsolutePath());
					dataPathText.setText(config.getDataFolderPath());
                    updateStatus();
				}
			}
		});
		
		saveDialog = new FileChooserDialog(this);
		saveDialog.loadFolder(config.getSaveFolderPath());
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
                config.setSaveFolderPath(file.getAbsolutePath());
				savePathText.setText(config.getSaveFolderPath());
			}
			
		});
		
		confDialog = new FileChooserDialog(this);
		confDialog.loadFolder(config.getConfFolderPath());
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
                config.setConfFolderPath(file.getAbsolutePath());
				confPathText.setText(config.getConfFolderPath());
			}
		});

        installDialog = new FileChooserDialog(this);
        if (config.hasOldFiles()) {
            installDialog.loadFolder(config.getOldFilesPath());
        } else {
            installDialog.loadFolder();
        }
        installDialog.setFolderMode(true);
        installDialog.setCanCreateFiles(false);
        installDialog.setShowOnlySelectable(true);
        installDialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
            @Override
            public void onFileSelected(final Dialog source, final File file) {
                DataCheckResult dcr = checker.checkWithPath(file.getAbsolutePath());
                if (dcr.isFound()) {
                    AlertDialog ad = new AlertDialog.Builder(source.getContext())
                            .setCancelable(true)
                            .setMessage("Are you sure you want to copy data from " + file.getAbsolutePath() + "?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    source.hide();
                                    copyData(file, new File(config.getDataFolderPath()));
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    new AlertDialog.Builder(source.getContext())
                            .setCancelable(false)
                            .setNeutralButton("OK", null)
                            .setTitle("Warning")
                            .setMessage("Could not find game data in this folder")
                            .show();
                }
            }

            @Override
            public void onFileSelected(Dialog source, File folder, String name) {
                // Do nothing.
            }
        });
	}
	
	// This cleans up all dialogs.
	private void dismissDialogs() {
		copyWarningDialog.dismiss();
		dataDialog.dismiss();
		saveDialog.dismiss();
		confDialog.dismiss();
        installDialog.dismiss();
	}
	
	// This will start the preloader, which will update the directories.
	public void applyButtonPress(View view) {
		config.save();
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
	protected void copyData(File inDir, File outDir)
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
					Log.i("DirsAsyncTask", "Calling copyFolder...");
                    for(String dirName: checker.getDirChecklist()) {
                        publishProgress("Copying " + dirName + "...");
                        File in = new File(arg0[0].getAbsolutePath() + "/" + dirName);
                        File out = new File(arg0[1].getAbsolutePath() + "/"
                                + checker.getInstallDir() + "/" + dirName.toUpperCase());
                        FilesystemHelper.copyFolder(in, out, true);
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
                updateStatus();
				Log.i("DirsConfigActivity", "Finishing asynctask...");	
			}
			
		}.execute(inDir, outDir);
	}

    private void updateStatus() {
        xcom1Status.setText("Status: checking...");
        new AsyncTask<Void, Void, Void>() {
            DataCheckResult result;
            Spanned resultDisplay;

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                xcom1Status.setText(resultDisplay);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (checker == null) {
                    checker = new Xcom1DataChecker();
                }
                result = checker.checkWithPath(config.getDataFolderPath() + "/UFO");
                if (result.isFound()) {
                    resultDisplay = Html.fromHtml("Status: <font color=\"#00FF00\">Version: " + result.getVersion() + " (" + result.getNotes() + ") </font>");

                } else {
                    resultDisplay = Html.fromHtml("Status: <font color=\"#FF0000\">Not found (" + result.getNotes() + ")</font>");
                }
                return null;
            }
        }.execute();
    }

}
