package creativitium.revolution.foundation;

import creativitium.revolution.foundation.base.MessageService;
import lombok.Getter;
import org.bukkit.NamespacedKey;
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

    @Override
    public void onLoad()
    {
        instance = this;
        commandLoader = new CommandLoader();
    }

    @Override
    public void onEnable()
    {
        messageService = coreServices.addService(NamespacedKey.fromString("fnd:messages"), new MessageService());
        coreServices.startServices();
        //--
        commandLoader.loadCommands("creativitium.revolution.foundation.commands", "foundation");
    }
}
