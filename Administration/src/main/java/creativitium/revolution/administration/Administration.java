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
import org.bukkit.NamespacedKey;
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
        commandSpyService = services.addService(NamespacedKey.fromString("adm:commandspy"), new CommandSpyService());
        adminChatService = services.addService(NamespacedKey.fromString("adm:adminchat"), new AdminChatService());
        banService = services.addService(NamespacedKey.fromString("adm:bans"), new BanService());
        invSeeService = services.addService(NamespacedKey.fromString("adm:invsee"), new InventorySeeService());
        blockingService = services.addService(NamespacedKey.fromString("adm:blocking"), new BlockingService());
        services.startServices();

        // Set up our commands
        commandLoader.loadCommandsManually("administration",
                new Command_adminchat(),
                new Command_ban(),
                new Command_banip(),
                new Command_bans(),
                new Command_blockcmd(),
                new Command_crash(),
                new Command_commandspy(),
                new Command_entitywipe(),
                new Command_freeze(),
                new Command_invsee(),
                new Command_kick(),
                new Command_multirun(),
                new Command_mute(),
                new Command_rawsay(),
                new Command_say(),
                new Command_smite(),
                new Command_stop(),
                new Command_sudo(),
                new Command_unban(),
                new Command_unbanip(),
                new Command_unblockcmd(),
                new Command_unmute(),
                new Command_whohas(),
                new Command_wildcard());
    }

    @Override
    public void onDisable()
    {
        services.stopServices();
    }
}
