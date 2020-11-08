package me.jacobtread.tells;

import org.apache.commons.exec.OS;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DriverUtils {

    private static final Logger LOGGER = TellonymBot.LOGGER;

    /**
     * Retrieves the gecko driver for selenium;
     *
     * @param purge Ignore the current driver if one exists and copy a new one.
     * @return The path to the driver
     */
    public static Path getDriver(boolean purge) {
        LOGGER.info("Determining driver to use");
        String name;
        boolean isWindows = OS.isFamilyWindows();
        if (OS.isFamilyMac()) {
            name = "driver-osx";
        } else if (OS.isFamilyWindows() || OS.isFamilyUnix()) {
            boolean is64Bit = System.getProperty("os.arch").toLowerCase().contains("64");
            name = "driver-" + (isWindows ? "win" : "linux") + (is64Bit ? "64" : "32") + (isWindows ? ".exe" : "");
        } else {
            LOGGER.severe("Unable to determine system type for driver!");
            return null;
        }
        Path driverPath = Paths.get("drivers/" + name);
        if (purge || !Files.exists(driverPath)) {
            if (extractDriver(DriverUtils.class.getResourceAsStream("/drivers/" + name), driverPath)) {
                return driverPath;
            } else {
                return null;
            }
        } else {
            return driverPath;
        }
    }

    /**
     * Copies a file from the input stream and marks it as executable
     *
     * @param inputStream The input stream to extract from
     * @param driverPath The path to extract to
     * @return Whether or not the driver was extracted
     */
    private static boolean extractDriver(InputStream inputStream, Path driverPath) {
        if (inputStream == null) {
            LOGGER.severe("Unable to extract driver, Internal file missing. Re-download bot.");
            return false;
        }
        try {
            Path parent = driverPath.getParent();
            LOGGER.info("Setting up driver directory structure...");
            if(!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            if(!Files.exists(driverPath)) {
                Files.createFile(driverPath);
            }
            LOGGER.info("Copying driver file...");
            Files.copy(inputStream, driverPath, StandardCopyOption.REPLACE_EXISTING);
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            LOGGER.info("Updating driver file permissions...");
            Files.setPosixFilePermissions(driverPath,perms);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to copy driver file from Jar.", e);
            return false;
        }
    }

}
