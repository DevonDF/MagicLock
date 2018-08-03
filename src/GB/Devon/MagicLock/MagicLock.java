package GB.Devon.MagicLock;

import GB.Devon.MagicLock.Uitl.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicLock extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        config = getConfig();
        saveDefaultConfig();
        ConfigManager.setConfigFile(config); // Give our configuration to the config manager for use elsewhere

        Bukkit.getPluginManager().registerEvents(new LockListener(), this); // Register our event manager
        getCommand("setowner").setExecutor(new DebugCommands());
        getCommand("setfriend").setExecutor(new DebugCommands());
        getCommand("magiclock").setExecutor(new HelpCommand());
    }

    @Override
    public void onDisable() {

    }

}
