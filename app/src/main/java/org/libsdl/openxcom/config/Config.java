package org.libsdl.openxcom.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

/**
 * Created by Alexey on 16.05.2015.
 *
 * Config - a class for storing some platform-specific launch-time and run-time
 * options.
 * This class is kinda singleton-ish, in a sense that it creates at most a single
 * instance of itself.
 * In order to create it a Context is needed. Since the lifetime of this object
 * may exceed that of the calling Context, the application context is used instead
 * to avoid memory leaks.
 */
public class Config {

    private static Config INSTANCE = null;

    /**
     * Returns the singleton instance, or null if the instance hasn't been created yet.
     * @return The Config object instance.
     */
    public static Config getInstance() {
        return INSTANCE;
    }

    /**
     * Creates the singleton instance if it hasn't been created yet. Returns the object's
     * instance if it has already been created.
     * @param context The context creating this object.
     * @return The object instance.
     * <p>
     * Note: The object tries to get the application context to avoid saving references
     * to activities.
     * </p>
     */
    public static Config createInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Config(context.getApplicationContext());
        }
        return INSTANCE;
    }

    /**
     * OpenXcom SharedPreferences config name.
     */
    public static final String SP_NAME = "OpenXcomPrefs";

    /**
     * Keys for SharedPreferences queries.
     */
    private static final String SP_DATA_PATH = "org.libsdl.openxcom.DATA_PATH";
    private static final String SP_SAVE_PATH = "org.libsdl.openxcom.SAVE_PATH";
    private static final String SP_CONF_PATH = "org.libsdl.openxcom.CONF_PATH";
    private static final String SP_DATA_CACHE = "org.libsdl.openxcom.DATA_CACHE";
    private static final String SP_SAVE_CACHE = "org.libsdl.openxcom.SAVE_CACHE";
    private static final String SP_CONF_CACHE = "org.libsdl.openxcom.CONF_CACHE";
    private static final String SP_UI_STYLE = "org.libsdl.openxcom.UI_STYLE";
    private static final String SP_ASSET_VERSION_PREFIX = "org.libsdl.openxcom.ASSET_VERSIONS.";

    /**
     * Legacy keys for SharedPreferences storage; should check those
     * for older versions.
     */
    private static final String OLD_USE_APP_CACHE_KEY = "useAppCache";
    private static final String OLD_USE_APP_CACHE_SAVE_KEY = "useAppCacheSave";
    private static final String OLD_USE_APP_CACHE_CONF_KEY = "useAppCacheConf";
    private static final String OLD_DATA_PATH_KEY = "dataPath";
    private static final String OLD_SAVE_PATH_KEY = "savePath";
    private static final String OLD_CONF_PATH_KEY = "confPath";
    private static final String OLD_UI_STYLE = "SystemUIStyle";

    private boolean mHasOldFiles;

    public boolean hasOldFiles() {
        return mHasOldFiles;
    }

    public String getOldFilesPath() {
        return mPrefs.getString(OLD_DATA_PATH_KEY, null);
    }

    public String getDataFolderPath() {
        return mDataFolderPath;
    }

    public void setDataFolderPath(String mDataFolderPath) {
        this.mDataFolderPath = mDataFolderPath;
    }

    public String getSaveFolderPath() {
        return mSaveFolderPath;
    }

    public void setSaveFolderPath(String mSaveFolderPath) {
        this.mSaveFolderPath = mSaveFolderPath;
    }

    public String getConfFolderPath() {
        return mConfFolderPath;
    }

    public void setConfFolderPath(String mConfFolderPath) {
        this.mConfFolderPath = mConfFolderPath;
    }

    public Boolean getUseDataCache() {
        return mUseDataCache;
    }

    public void setUseDataCache(Boolean mUseDataCache) {
        this.mUseDataCache = mUseDataCache;
    }

    public Boolean getUseSaveCache() {
        return mUseSaveCache;
    }

    public void setUseSaveCache(Boolean mUseSaveCache) {
        this.mUseSaveCache = mUseSaveCache;
    }

    public Boolean getUseConfCache() {
        return mUseConfCache;
    }

    public void setUseConfCache(Boolean mUseConfCache) {
        this.mUseConfCache = mUseConfCache;
    }

    public int getSystemUiStyle() {
        return mSystemUiStyle;
    }

    public void setSystemUiStyle(int mSystemUiStyle) {
        this.mSystemUiStyle = mSystemUiStyle;
    }

    private String mDataFolderPath;
    private String mSaveFolderPath;
    private String mConfFolderPath;
    private Boolean mUseDataCache;
    private Boolean mUseSaveCache;
    private Boolean mUseConfCache;
    private int mSystemUiStyle;

    private SharedPreferences mPrefs;
    /**
     * The context is stored for getting application's private directories.
     */
    private Context mContext;

    public File getExternalFilesDir() {
        return mContext.getExternalFilesDir(null);
    }

    public String getAssetVersion(String assetName) {
        return mPrefs.getString(SP_ASSET_VERSION_PREFIX + assetName, "");
    }

    public void setAssetVersion(String assetName, String version) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(SP_ASSET_VERSION_PREFIX + assetName, version);
        edit.apply();
    }

    private Config(Context context) {
        mContext = context;
        mPrefs = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        loadPrefs();
    }

    /**
     * Loads saved values from the local storage.
     * Should only be called once by constructor.
     */
    private void loadPrefs() {
        final String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/openxcom";
        if (mPrefs.getString(OLD_DATA_PATH_KEY, null) != null) {
            String oldPath = mPrefs.getString(OLD_DATA_PATH_KEY, "");
            mHasOldFiles = new File(oldPath + "/UFO/TERRAIN/UFO1.PCK").exists();
        } else {
            mHasOldFiles = false;
        }
        mDataFolderPath = mPrefs.getString(SP_DATA_PATH, defaultPath);
        mSaveFolderPath = mPrefs.getString(SP_SAVE_PATH, defaultPath);
        mConfFolderPath = mPrefs.getString(SP_CONF_PATH, defaultPath);
        mUseDataCache = mPrefs.getBoolean(SP_DATA_CACHE, false);
        mUseSaveCache = mPrefs.getBoolean(SP_SAVE_CACHE, false);
        mUseConfCache = mPrefs.getBoolean(SP_CONF_CACHE, false);
        mSystemUiStyle = mPrefs.getInt(SP_UI_STYLE, 0);
    }

    /**
     * Saves current configuration values to associated sharedPreferences.
     * Should be called after changes to the Config object.
     */
    public void save() {
        SharedPreferences.Editor prefsEdit = mPrefs.edit();
        prefsEdit.putString(SP_DATA_PATH, mDataFolderPath);
        prefsEdit.putString(SP_SAVE_PATH, mSaveFolderPath);
        prefsEdit.putString(SP_CONF_PATH, mConfFolderPath);
        prefsEdit.putBoolean(SP_DATA_CACHE, mUseDataCache);
        prefsEdit.putBoolean(SP_SAVE_CACHE, mUseSaveCache);
        prefsEdit.putBoolean(SP_CONF_CACHE, mUseConfCache);
        prefsEdit.putInt(SP_UI_STYLE, mSystemUiStyle);
        prefsEdit.apply();
    }
}
