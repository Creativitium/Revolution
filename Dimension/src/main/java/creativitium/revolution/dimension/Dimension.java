package creativitium.revolution.dimension;

import creativitium.revolution.dimension.commands.Command_time;
import creativitium.revolution.dimension.commands.Command_weather;
import creativitium.revolution.dimension.services.BasicWorldProtection;
import creativitium.revolution.dimension.services.WorldManager;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import org.bukkit.NamespacedKey;
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

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        // Import the messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Setup our services
        worldManager = group.addService(NamespacedKey.fromString("dim:worlds"), new WorldManager());
        basicProtection = group.addService(NamespacedKey.fromString("dim:protection"), new BasicWorldProtection());
        group.startServices();

        // Add additional commands
        Foundation.getInstance().getCommandLoader().loadCommandsManually("dimension",
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
