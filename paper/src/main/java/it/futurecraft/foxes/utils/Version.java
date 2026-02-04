package it.futurecraft.foxes.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public enum Version {
    v1_21_R6("v1_21_R6"),
    v1_21_R7("v1_21_R7"),
    UNKNOWN(null);

    @Nullable
    public final String version;

    Version(@Nullable String version) {
        this.version = version;
    }

    public static Version getServerVersion() {
        String version = Bukkit.getVersion();

        int i = version.indexOf("(MC: ");
        int j = version.indexOf(")");

        String mcVersion = version.substring(i + 7, j);
        double versionDouble = Double.parseDouble(mcVersion);

        if (versionDouble == 21.1D) {
            return Version.v1_21_R6;
        }

        // 1.21.11 is still 1.21R6 but has some changes on packages and obfuscation so need new module
        if (versionDouble == 21.11D) {
            return Version.v1_21_R7;
        }

        return UNKNOWN;
    }
}
