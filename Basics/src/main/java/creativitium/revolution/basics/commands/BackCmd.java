package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "back",
        description = "Return to your last location.",
        usage = "/back",
        permission = "basics.command.back",
        source = SourceType.ONLY_IN_GAME)
public class BackCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final BPlayer data = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(playerSender.getUniqueId());

        if (data.getLastLocation() != null)
        {
            msg(playerSender, "basics.command.back.teleporting");
            playerSender.teleportAsync(data.getLastLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }
        else
        {
            msg(playerSender, "basics.command.back.no_place_to_go");
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
