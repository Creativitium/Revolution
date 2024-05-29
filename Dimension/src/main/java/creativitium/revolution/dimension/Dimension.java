package creativitium.revolution.dimension;

import creativitium.revolution.dimension.commands.Command_time;
import creativitium.revolution.dimension.commands.Command_weather;
import creativitium.revolution.dimension.services.BasicWorldProtection;
import creativitium.revolution.dimension.services.WorldManager;
import creativitium.revolution.foundation.CommandLoader;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.java.JavaPlugin;

public class Dimension extends JavaPlugin
{
    @Getter
    private static Dimension instance;

    @Getter
    private final RServiceGroup group = new RServiceGroup();
    @Getter
    private WorldManager worldManager;
    @Getter
    private BasicWorldProtection basicProtection;

    @Getter
    private CommandLoader commandLoader;

    @Override
    public void onLoad()
    {
        instance = this;
        commandLoader = new CommandLoader();
    }

    @Override
    public void onEnable()
    {
        // Import the messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Setup our services
        worldManager = group.addService(Key.key("dim", "worlds"), new WorldManager());
        basicProtection = group.addService(Key.key("dim", "protection"), new BasicWorldProtection());
        group.startServices();

        // Add additional commands
        commandLoader.loadCommandsManually("dimension",
                new Command_time(),
                new Command_weather()
        );
    }

    @Override
    public void onDisable()
    {
        group.stopServices();
    }
}
