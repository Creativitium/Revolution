package creativitium.revolution.capitalism;

import creativitium.revolution.capitalism.commands.Command_balance;
import creativitium.revolution.capitalism.commands.Command_eco;
import creativitium.revolution.capitalism.commands.Command_pay;
import creativitium.revolution.capitalism.data.CPlayerService;
import creativitium.revolution.foundation.CommandLoader;
import creativitium.revolution.foundation.Foundation;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Capitalism extends JavaPlugin
{
    @Getter
    private static Capitalism instance;
    //--
    private CPlayerService playerService;
    //--
    private CommandLoader commandLoader;

    @Override
    public void onLoad()
    {
        instance = this;
        commandLoader = new CommandLoader();
    }

    @Override
    public void onEnable()
    {
        // Import our messages
        Foundation.getInstance().getMessageService().importFrom(this);

        // Register our services
        playerService = new CPlayerService();
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(Key.key("capitalism", "primary"), playerService);
        Bukkit.getServicesManager().register(Economy.class, playerService.getEconomy(), this, ServicePriority.Highest);

        // Register our commands
        commandLoader.loadCommandsManually("capitalism",
                new Command_balance(),
                new Command_eco(),
                new Command_pay());
    }
}
