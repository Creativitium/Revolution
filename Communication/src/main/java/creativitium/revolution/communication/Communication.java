package creativitium.revolution.communication;

import creativitium.revolution.communication.boilerplate.Communicator;
import creativitium.revolution.communication.platforms.Discord;
import creativitium.revolution.communication.platforms.Minecraft;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Communication extends JavaPlugin
{
    @Getter
    private static Communication instance;

    @Getter
    private final RServiceGroup communicators = new RServiceGroup();

    @Override
    public void onLoad()
    {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
    }

    @Override
    public void onEnable()
    {
        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Minecraft
        communicators.addService(NamespacedKey.fromString("communication:minecraft"), new Minecraft());

        // Discord
        if (getConfig().getBoolean("discord.enabled", false))
            communicators.addService(NamespacedKey.fromString("communication:discord"), new Discord());

        // Start everything up
        communicators.startServices();
    }

    @Override
    public void onDisable()
    {
        // Shut down everything
        communicators.stopServices();
    }
}
