package creativitium.revolution.basics;

import creativitium.revolution.basics.command.CustomCommandLoader;
import creativitium.revolution.basics.commands.*;
import creativitium.revolution.basics.data.BPlayerService;
import creativitium.revolution.basics.services.BasicsService;
import creativitium.revolution.basics.services.ExtrasService;
import creativitium.revolution.basics.services.WarpsService;
import creativitium.revolution.foundation.CommandLoader;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
    @Getter
    private ExtrasService extrasService;
    //--
    private CommandLoader commandLoader;

    @Override
    public void onLoad()
    {
        instance = this;
        this.saveDefaultConfig();
        commandLoader = new CommandLoader();
    }

    @Override
    public void onEnable()
    {
        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Set up our player data service
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(Key.key("basics", "primary"), new BPlayerService());

        // Set up our other services
        basicsService = services.addService(Key.key("basics", "main"), new BasicsService());
        warpsService = services.addService(Key.key("basics", "warps"), new WarpsService());
        extrasService = services.addService(Key.key("basics", "extras"), new ExtrasService());
        services.startServices();

        // Set up our commands
        commandLoader.loadCommandsManually("basics",
                new BackCmd(),
                new BerserkCmd(),
                new ClearCmd(),
                new DeafenCmd(),
                new DelHomeCmd(),
                new DelWarpCmd(),
                new FlyCmd(),
                new GamemodeCmd(),
                new GamemodeCmd(GameMode.ADVENTURE),
                new GamemodeCmd(GameMode.CREATIVE),
                new GamemodeCmd(GameMode.SURVIVAL),
                new GamemodeCmd(GameMode.SPECTATOR, (short) 2),
                new GiveCmd(),
                new GodCmd(),
                new HealCmd(),
                new HomeCmd(),
                new ItemCmd(),
                new KillCmd(),
                new ListCmd(),
                new MailCmd(),
                new MessageCmd(),
                new NearCmd(),
                new NicknameCmd(),
                new RealNameCmd(),
                new ReplyCmd(),
                new SeenCmd(),
                new SetHomeCmd(),
                new SetSpawnCmd(),
                new SetWarpCmd(),
                new SpawnCmd(),
                new SpawnMobCmd(),
                new SpeedCmd(),
                new TagCmd(),
                new TeleportCmd(),
                new TopCmd(),
                new TPHereCmd(),
                new TPOCmd(),
                new TPOHereCmd(),
                new TPPosCmd(),
                new TPToggleCmd(),
                new WarpCmd(),
                new WorldCmd());

        // Set up our custom commands
        new CustomCommandLoader(new File(getDataFolder(), "commands")).loadCommands();
    }

    @Override
    public void onDisable()
    {
        services.stopServices();
    }
}
