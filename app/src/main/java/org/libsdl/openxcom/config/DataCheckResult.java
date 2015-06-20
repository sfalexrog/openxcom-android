package org.libsdl.openxcom.config;

/**
 * The result of checking data. This object contains flag of whether the game data
 * was found in the specified folder, version string and additional notes (if any).
 * This object is normally constructed by DataChecker, and you don't need to
 * create it yourself.
 * Created by Alexey on 13.06.2015.
 */
public class DataCheckResult {
    private final boolean found;
    private final String version;
    private final String notes;

    /**
     * Was the game data present at the specified location?
     * @return true if the game data was present.
     */
    public boolean isFound() {
        return found;
    }

    /**
     * Which version corresponds to the data?
     * @return The short version description.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets any additional noteworthy information about the game files.
     * @return Additional notes on the game version.
     */
    public String getNotes() {
        return notes;
    }

    public DataCheckResult(boolean found, String version, String notes) {
        this.found = found;
        this.version = version;
        this.notes = notes;
    }
}
