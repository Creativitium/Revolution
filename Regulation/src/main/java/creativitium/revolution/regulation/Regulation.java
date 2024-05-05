package creativitium.revolution.regulation;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import creativitium.revolution.regulation.commands.Command_toggle;
import creativitium.revolution.regulation.services.WorldRegulator;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Regulation extends JavaPlugin
{
    @Getter
    private static Regulation instance;
    //--
    @Getter
    private RServiceGroup services = new RServiceGroup();
    @Getter
    private WorldRegulator worldRegulator;

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Set up our services
        this.worldRegulator = services.addService(NamespacedKey.fromString("regulation:regulator"), new WorldRegulator(getDataFolder()));
        this.services.startServices();

        // Set up our commands
        Foundation.getInstance().getCommandLoader().loadCommandsManually("regulation", new Command_toggle());
    }

    @Override
    public void onDisable()
    {
    }
}
