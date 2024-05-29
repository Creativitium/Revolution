package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "top",
        description = "Teleport to the highest surface of your current location.",
        usage = "/top",
        permission = "basics.command.top",
        source = SourceType.ONLY_IN_GAME)
public class TopCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        msg(sender, "basics.command.top.teleporting");
        playerSender.teleportAsync(playerSender.getLocation().toHighestLocation().add(0, 1, 0), PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
