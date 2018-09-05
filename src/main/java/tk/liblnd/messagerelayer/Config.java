package tk.liblnd.messagerelayer;

/**
 * @author Artuto
 */

import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
    private final FileConfiguration config;

    Config(FileConfiguration config)
    {
        this.config = config;
        config.addDefault("webhookUrl", "https://canary.discordapp.com/api/webhooks");
        config.options().copyDefaults(true);
    }

    public String getUrl()
    {
        return config.getString("webhookUrl");
    }
}
