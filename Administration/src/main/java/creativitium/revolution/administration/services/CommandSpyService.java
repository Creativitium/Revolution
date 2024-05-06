package creativitium.revolution.administration.services;

import creativitium.revolution.administration.data.APlayer;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.Shortcuts;
import creativitium.revolution.administration.Administration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpyService extends RService
{
    public CommandSpyService()
    {
        super(Administration.getInstance());
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        Player sender = event.getPlayer();

        Bukkit.getOnlinePlayers().stream().filter(player -> !player.getUniqueId().equals(sender.getUniqueId())
                && player.hasPermission("revolution.command.commandspy")
                && ((APlayer) Shortcuts.getExternalPlayerService(Administration.getInstance())
                        .getPlayerData(player.getUniqueId())).isCommandSpyEnabled()).forEach(player ->
                player.sendMessage(getMsg("administration.components.commandspy",
                        Placeholder.parsed("name", sender.getName()),
                        Placeholder.unparsed("command", event.getMessage()))));
    }
}
