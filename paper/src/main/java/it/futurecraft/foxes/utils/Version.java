package it.futurecraft.foxes.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public enum Version {
    v1_21_R6("v1_21_R6"),
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

        return UNKNOWN;
    }
}
