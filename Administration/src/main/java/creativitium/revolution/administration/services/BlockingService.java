package creativitium.revolution.administration.services;

import creativitium.revolution.administration.data.SPlayer;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.Shortcuts;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BlockingService extends RService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event)
    {
        final Player player = event.getPlayer();
        final SPlayer data = (SPlayer) Shortcuts.getService(Key.key("administration", "sanctions")).getPlayerData(player.getUniqueId());

        if (data.isMuted())
        {
            if (player.hasPermission("administration.bypass.mutes"))
            {
                data.setMuted(false);
                return;
            }

            player.sendMessage(getMsg("administration.command.mute.you_have_been_muted"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        final Player player = event.getPlayer();
        final SPlayer data = (SPlayer) Shortcuts.getService(Key.key("administration", "sanctions")).getPlayerData(player.getUniqueId());

        if (data.isCommandsBlocked())
        {
            if (player.hasPermission("administration.bypass.blocked_commands"))
            {
                data.setCommandsBlocked(false);
                return;
            }

            player.sendMessage(getMsg("administration.command.blockcmd.your_commands_have_been_blocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Player player = event.getPlayer();
        final SPlayer data = (SPlayer) Shortcuts.getService(Key.key("administration", "sanctions")).getPlayerData(player.getUniqueId());

        if (data.isFrozen())
        {
            if (player.hasPermission("administration.bypass.freeze"))
            {
                data.setFrozen(false);
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        final Player player = event.getPlayer();
        final SPlayer data = (SPlayer) Shortcuts.getService(Key.key("administration", "sanctions")).getPlayerData(player.getUniqueId());

        if (data.isFrozen())
        {
            if (player.hasPermission("administration.bypass.freeze"))
            {
                data.setFrozen(false);
                return;
            }

            event.setCancelled(true);
        }
    }
}