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
                getSLF4JLogger().warn("You are currently using a development build of Foundation. Assuming that this is a development environment, here is some advice:");
                getSLF4JLogger().warn("- Foundation should remain fundamentally the same and should not have any drastic changes. If you make such changes anyways, either try to keep the original functions around to retain backwards compatibility or update the plugins that relied on the older functionality");
                getSLF4JLogger().warn("- If you make changes to other subprojects that include changes to Foundation itself, don't forget to replace Foundation as well when you go to run it on a test server");
                getSLF4JLogger().warn("- Due to how the custom message system works, there is no \"automatic updates\" functionality for messages.json and prefixes.json - you will need to regenerate the files by deleting the files from their relevant folders and using /messages reload");
                getSLF4JLogger().warn("- Do not mix builds from different branches together as this could cause stability/compatibility issues");
                getSLF4JLogger().warn("- Make sure you don't have multiple versions of the same plugin installed as this will cause all kinds of issues");
                getSLF4JLogger().warn("- Do not use builds intended for development environments in production environments as this could cause stability/compatibility/security issues");
                getSLF4JLogger().warn("- If you are seeing this message in a production environment even though you're sure you shouldn't be, make sure that the current branch is set to \"main\" and that any changes you made have been committed and then try compiling again");
                getSlf4jLogger().warn("With all that said, here are the build details: \n{}", version);
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
