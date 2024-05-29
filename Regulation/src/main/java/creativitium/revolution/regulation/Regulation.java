package creativitium.revolution.regulation;

import creativitium.revolution.foundation.CommandLoader;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import creativitium.revolution.regulation.commands.ToggleCmd;
import creativitium.revolution.regulation.services.GlobalRegulator;
import creativitium.revolution.regulation.services.WorldRegulator;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.java.JavaPlugin;

public class Regulation extends JavaPlugin
{
    @Getter
    private static Regulation instance;
    //--
    @Getter
    private final RServiceGroup services = new RServiceGroup();
    @Getter
    private WorldRegulator worldRegulator;
    @Getter
    private GlobalRegulator globalRegulator;
    //--
    private CommandLoader commandLoader;

    @Override
    public void onLoad()
    {
        instance = this;
        saveDefaultConfig();
        commandLoader = new CommandLoader();
    }

    @Override
    public void onEnable()
    {
        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Set up our services
        this.worldRegulator = services.addService(Key.key("regulation", "regulator"), new WorldRegulator(getDataFolder()));
        this.globalRegulator = services.addService(Key.key("regulation", "global"), new GlobalRegulator());
        this.services.startServices();

        // Set up our commands
        commandLoader.loadCommandsManually("regulation", new ToggleCmd());
    }
}
