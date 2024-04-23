package creativitium.revolution.administration;

import creativitium.revolution.administration.commands.Command_adminchat;
import creativitium.revolution.administration.commands.Command_commandspy;
import creativitium.revolution.administration.data.APlayer;
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
        services.startServices();

        // Set up our commands
        /* This just doesn't work at all. None of the commands get registered when we do it with this method, and yet
         *  somehow, Reflections reports that it found 94 urls... how?! There isn't even a good
         */
        // This doesn't work for some reason. It just says "it took X ms to scan 94 urls producing 0 keys and 0 values",
        //  but there aren't 94 classes at all? WTF?
        //Foundation.getInstance().getCommandLoader().loadCommands("creativitium.revolution.administration.commands", "admin");
        Foundation.getInstance().getCommandLoader().loadCommandsManually("administration",
                new Command_adminchat(), new Command_commandspy());
    }
}
