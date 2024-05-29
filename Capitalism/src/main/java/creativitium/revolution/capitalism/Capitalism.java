package creativitium.revolution.capitalism;

import creativitium.revolution.capitalism.commands.BalanceCmd;
import creativitium.revolution.capitalism.commands.EcoCmd;
import creativitium.revolution.capitalism.commands.PayCmd;
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
        //--
        CPlayerService playerService = new CPlayerService();
        Foundation.getInstance().getPlayerDataService().addExternalPlayerService(Key.key("capitalism", "primary"), playerService);
        Bukkit.getServicesManager().register(Economy.class, playerService.getEconomy(), this, ServicePriority.Highest);

        // Register our commands
        commandLoader.loadCommandsManually("capitalism",
                new BalanceCmd(),
                new EcoCmd(),
                new PayCmd());
    }
}
