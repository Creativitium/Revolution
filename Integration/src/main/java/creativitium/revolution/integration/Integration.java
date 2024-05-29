package creativitium.revolution.integration;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import creativitium.revolution.integration.discord.Adm2DiscordSRV;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Integration extends JavaPlugin
{
    @Getter
    private static Integration instance;

    // Discord-related plugins
    private final RServiceGroup discordPlugins = new RServiceGroup();

    @Override
    public void onLoad()
    {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        final PluginManager manager = Bukkit.getPluginManager();

        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Sets up our integrations
        if (manager.isPluginEnabled("DiscordSRV") && manager.isPluginEnabled("Administration"))
        {
            discordPlugins.addService(Key.key("integration", "discordsrv_administration"), new Adm2DiscordSRV());
        }

        // Start our groups of plugins
        discordPlugins.startServices();
    }
}
