package creativitium.revolution.foundation;

import creativitium.revolution.foundation.base.MessageService;
import creativitium.revolution.foundation.base.PlayerDataService;
import creativitium.revolution.foundation.hook.VaultHook;
import creativitium.revolution.foundation.utilities.BuildVersion;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Foundation extends JavaPlugin
{
    @Getter
    private static Foundation instance;
    @Getter
    private static final Logger slf4jLogger = LoggerFactory.getLogger("Revolution");
    //--
    @Getter
    private CommandLoader commandLoader;
    //--
    @Getter
    private final RServiceGroup coreServices = new RServiceGroup();
    @Getter
    private MessageService messageService;
    @Getter
    private PlayerDataService playerDataService;
    //--
    @Getter
    private final RServiceGroup hooks = new RServiceGroup();
    @Getter
    private VaultHook vaultHook;

    @Override
    public void onLoad()
    {
        instance = this;
        commandLoader = new CommandLoader();

        BuildVersion.getVersion(this).ifPresent(version ->
        {
            if (version.isDevelopmentBuild())
            {
                getSLF4JLogger().warn("You are currently using a development build of Foundation.");
                getSLF4JLogger().warn("This build should not be used in a production environment as it may cause stability or compatibility issues.");
                getSlf4jLogger().warn("Here are details about this build: \n{}", version);
            }
        });
    }

    @Override
    public void onEnable()
    {
        messageService = coreServices.addService(Key.key("fnd", "messages"), new MessageService());
        playerDataService = coreServices.addService(Key.key("fnd", "playerdata"), new PlayerDataService());
        coreServices.startServices();
        //--
        vaultHook = hooks.addService(Key.key("fnd", "vault"), new VaultHook());
        hooks.startServices();
        //--
        commandLoader.loadCommands("creativitium.revolution.foundation.commands", "foundation");
    }

    @Override
    public void onDisable()
    {
        coreServices.stopServices();
    }
}
