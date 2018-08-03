package GB.Devon.MagicLock.Uitl;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void setConfigFile(FileConfiguration config) {
        ConfigManager.config = config;
    }

    public static String getString(String name) {
        return config.getString(name);
    }

    public static int getInt(String name) {
        return config.getInt(name);
    }

    public static boolean getBool(String name) {
        return config.getBoolean(name);
    }

}
