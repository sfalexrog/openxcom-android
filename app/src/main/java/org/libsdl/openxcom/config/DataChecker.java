package org.libsdl.openxcom.config;

import java.util.Set;

/**
 * A data checker interface. Implementations of this interface should check for game data
 * and return the corresponding DataCheckResult object.
 * Created by Alexey on 13.06.2015.
 */
public interface DataChecker {
    /**
     * Checks data at the supplied path.
     * @param path Path to folder containing game files.
     * @return DataCheckResult object containing check status
     * and brief version desctiption.
     */
    DataCheckResult checkWithPath(String path);

    /**
     * Gets a set of subdirectories that are scanned during checking.
     * @return A set of subdirectory names.
     */
    Set<String> getDirChecklist();

    /**
     * Gets the name of subdirectory in the data folder where the game files should be put.
     * @return The name of the subdirectory ("UFO", "TFTD", etc).
     */
    String getInstallDir();
}
