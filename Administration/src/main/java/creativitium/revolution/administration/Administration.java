package creativitium.revolution.administration;

import creativitium.revolution.administration.commands.*;
import creativitium.revolution.administration.data.SPlayerService;
import creativitium.revolution.administration.services.*;
import creativitium.revolution.foundation.CommandLoader;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import creativitium.revolution.administration.data.APlayerService;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.java.JavaPlugin;

public class Administration extends JavaPlugin
{
    @Getter
    private static Administration instance;
    //--
    @Getter
    private final RServiceGroup services = new RServiceGroup();
    @Getter
    private CommandSpyService commandSpyService;
    @Getter
    private AdminChatService adminChatService;
    @Getter
    private BanService banService;
    @Getter
    private InventorySeeService invSeeService;
    @Getter
    private BlockingService blockingService;
    //--
    private CommandLoader commandLoader;

    @Override
    public void onLoad()
    {
        instance = this;

        // Save our configuration if it doesn't exist already
        this.saveDefaultConfig();

        // Initialize our own instance of CommandLoader
        commandLoader = new CommandLoader();
    }

    @Override
    public void onEnable()
    {
        // Load the configuration
        this.reloadConfig();

        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Set up our player data services
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(Key.key("administration", "admin_preferences"), new APlayerService());
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(Key.key("administration", "sanctions"), new SPlayerService());

        // Set up our other services
        commandSpyService = services.addService(Key.key("adm", "commandspy"), new CommandSpyService());
        adminChatService = services.addService(Key.key("adm", "adminchat"), new AdminChatService());
        banService = services.addService(Key.key("adm", "bans"), new BanService());
        invSeeService = services.addService(Key.key("adm", "invsee"), new InventorySeeService());
        blockingService = services.addService(Key.key("adm", "blocking"), new BlockingService());
        services.startServices();

        // Set up our commands
        commandLoader.loadCommandsManually("administration",
                new AdminChatCmd(),
                new BanCmd(),
                new BanIpCmd(),
                new BansCmd(),
                new BlockcmdCmd(),
                new CrashCmd(),
                new CommandSpyCmd(),
                new EntityWipeCmd(),
                new FreezeCmd(),
                new InvseeCmd(),
                new KickCmd(),
                new MultirunCmd(),
                new MuteCmd(),
                new RawSayCmd(),
                new SayCmd(),
                new SmiteCmd(),
                new StopCmd(),
                new SudoCmd(),
                new UnbanCmd(),
                new UnbanIPCmd(),
                new UnblockcmdCmd(),
                new UnmuteCmd(),
                new WhoHasCmd(),
                new WildcardCmd());
    }

    @Override
    public void onDisable()
    {
        services.stopServices();
    }
}
