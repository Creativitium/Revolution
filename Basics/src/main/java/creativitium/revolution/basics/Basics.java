package creativitium.revolution.basics;

import creativitium.revolution.basics.commands.*;
import creativitium.revolution.basics.data.BPlayerService;
import creativitium.revolution.basics.services.BasicsService;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Basics extends JavaPlugin
{
    @Getter
    private static Basics instance;
    //--
    @Getter
    private final RServiceGroup services = new RServiceGroup();
    @Getter
    private BasicsService basicsService;

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

        // Set up our player data service
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(this, new BPlayerService());

        // Set up our other services
        basicsService = services.addService(NamespacedKey.fromString("basics:main"), new BasicsService());
        services.startServices();

        // Set up our commands
        Foundation.getInstance().getCommandLoader().loadCommandsManually("basics",
                new Command_berserk(),
                new Command_delhome(),
                new Command_god(),
                new Command_home(),
                new Command_kill(),
                new Command_sethome());
    }
}
