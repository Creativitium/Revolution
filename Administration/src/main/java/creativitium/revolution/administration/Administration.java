package creativitium.revolution.administration;

import creativitium.revolution.administration.commands.*;
import creativitium.revolution.administration.services.AdminChatService;
import creativitium.revolution.administration.services.BanService;
import creativitium.revolution.administration.services.CommandSpyService;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import creativitium.revolution.administration.data.APlayerService;
import lombok.Getter;
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
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(this, new APlayerService());

        // Set up our other services
        commandSpyService = services.addService(NamespacedKey.fromString("adm:commandspy"), new CommandSpyService());
        adminChatService = services.addService(NamespacedKey.fromString("adm:adminchat"), new AdminChatService());
        banService = services.addService(NamespacedKey.fromString("adm:bans"), new BanService());
        services.startServices();

        // Set up our commands
        /* This just doesn't work at all. None of the commands get registered when we do it with this method, and yet
         *  somehow, Reflections reports that it found 94 urls... how?! */
        //Foundation.getInstance().getCommandLoader().loadCommands("creativitium.revolution.administration.commands", "admin");
        Foundation.getInstance().getCommandLoader().loadCommandsManually("administration",
                new Command_adminchat(),
                new Command_ban(),
                new Command_crash(),
                new Command_commandspy(),
                new Command_say(),
                new Command_smite(),
                new Command_unban(),
                new Command_whohas());
    }

    @Override
    public void onDisable()
    {
        services.stopServices();
    }
}
