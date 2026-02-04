package it.futurecraft.foxes.utils;

import org.bukkit.Bukkit;

public enum Version {
    v1_21_10,
    v1_21_11,
    UNKNOWN;


    public static Version getServerVersion() {
        String version = Bukkit.getVersion();

        int i = version.indexOf("(MC: ");
        int j = version.indexOf(")");

        String mcVersion = version.substring(i + 7, j);
        double versionDouble = Double.parseDouble(mcVersion);

        if (versionDouble == 21.1D) {
            return Version.v1_21_10;
        }

        if (versionDouble == 21.11D) {
            return Version.v1_21_11;
        }

        return UNKNOWN;
    }
}
