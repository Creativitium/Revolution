package creativitium.revolution.basics.services;

import creativitium.revolution.foundation.templates.RService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ExtrasService extends RService
{
    @EventHandler
    public void onServerPing(ServerListPingEvent event)
    {
        if (Bukkit.hasWhitelist())
        {
            event.motd(getMsg("basics.components.motd.whitelisted"));
        }
        else if (event.getNumPlayers() == Bukkit.getMaxPlayers())
        {
            event.motd(getMsg("basics.components.motd.full"));
        }
        else
        {
            event.motd(getMsg("basics.components.motd.normal",
                    Placeholder.unparsed("version", Bukkit.getMinecraftVersion())));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.getPlayer().sendPlayerListHeaderAndFooter(getMsg("basics.components.tablist.header"), getMsg("basics.components.tablist.footer"));
    }
}
