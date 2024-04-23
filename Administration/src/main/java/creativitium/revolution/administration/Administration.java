package creativitium.revolution.administration;

import creativitium.revolution.administration.commands.Command_adminchat;
import creativitium.revolution.administration.commands.Command_commandspy;
import creativitium.revolution.administration.data.APlayer;
import creativitium.revolution.administration.services.AdminChatService;
import creativitium.revolution.administration.services.CommandSpyService;
import creativitium.revolution.foundation.CommandLoader;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.RServiceGroup;
import creativitium.revolution.administration.data.APlayerService;
import creativitium.revolution.foundation.command.RCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

public class Administration extends JavaPlugin
{
    @Getter
    private static Administration instance;
    //--
    @Getter
    private RServiceGroup services = new RServiceGroup();
    @Getter
    private CommandSpyService commandSpyService;
    @Getter
    private AdminChatService adminChatService;

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
        services.startServices();

        // Set up our commands
        /* This just doesn't work at all. None of the commands get registered when we do it with this method, and yet
         *  somehow, Reflections reports that it found 94 urls... how?! */
        //Foundation.getInstance().getCommandLoader().loadCommands("creativitium.revolution.administration.commands", "admin");
        Foundation.getInstance().getCommandLoader().loadCommandsManually("administration",
                new Command_adminchat(), new Command_commandspy());
    }
}
