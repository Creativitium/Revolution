package creativitium.revolution.administration.services;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.templates.RService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventorySeeService extends RService
{
    public InventorySeeService()
    {
        super(Administration.getInstance());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return;

        final Player player = (Player) event.getWhoClicked();
        final Inventory clickedInventory = event.getClickedInventory();

        if (player != clickedInventory.getHolder() && clickedInventory.getHolder() instanceof Player target
                && Bukkit.getOnlinePlayers().contains(target)
                && !player.hasPermission("administration.invsee.modify"))
        {
            event.setCancelled(true);
        }
    }
}
