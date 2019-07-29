package xyz.liblnd.messagerelayer;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Artuto
 */

class Config
{
    private final FileConfiguration config;

    Config(FileConfiguration config)
    {
        this.config = config;
        config.addDefault("webhookUrl", "https://canary.discordapp.com/api/webhooks");
        config.options().copyDefaults(true);
    }

    String getUrl()
    {
        return config.getString("webhookUrl");
    }
}
