package creativitium.revolution.basics;

import creativitium.revolution.basics.commands.*;
import creativitium.revolution.basics.data.BPlayerService;
import creativitium.revolution.basics.services.BasicsService;
import creativitium.revolution.basics.services.WarpsService;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import org.bukkit.GameMode;
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
    @Getter
    private WarpsService warpsService;

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
        warpsService = services.addService(NamespacedKey.fromString("basics:warps"), new WarpsService());
        services.startServices();

        // Set up our commands
        Foundation.getInstance().getCommandLoader().loadCommandsManually("basics",
                new Command_back(),
                new Command_berserk(),
                new Command_delhome(),
                new Command_delwarp(),
                new Command_fly(),
                new Command_gamemode(),
                new Command_gamemode(GameMode.ADVENTURE),
                new Command_gamemode(GameMode.CREATIVE),
                new Command_gamemode(GameMode.SURVIVAL),
                new Command_gamemode(GameMode.SPECTATOR, (short) 2),
                new Command_god(),
                new Command_heal(),
                new Command_home(),
                new Command_kill(),
                new Command_near(),
                new Command_nickname(),
                new Command_realname(),
                new Command_seen(),
                new Command_sethome(),
                new Command_setwarp(),
                new Command_tag(),
                new Command_teleport(),
                new Command_warp(),
                new Command_world());
    }

    @Override
    public void onDisable()
    {
        services.stopServices();
    }
}
