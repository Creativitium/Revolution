package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "tpohere",
        description = "Teleport a player to you, overriding their teleportation status.",
        usage = "/tpohere <player>",
        aliases = {"teleportoverridehere"},
        permission = "basics.command.tpohere",
        source = SourceType.ONLY_IN_GAME)
public class Command_tpohere extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final Optional<Player> target = getPlayer(args[0]);

        if (target.isPresent() && target.stream().anyMatch(player -> player == playerSender))
        {
            msg(sender, "basics.command.teleport.cannot_teleport_to_yourself");
            return true;
        }

        target.ifPresentOrElse(player ->
        {
            msg(sender, "basics.command.teleport.teleporting_to_you", Placeholder.unparsed("player", player.getName()));
            msg(player, "basics.command.teleport.teleporting", Placeholder.unparsed("player", sender.getName()));
            player.teleportAsync(playerSender.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        // We're not fucking around
        if (args.length != 1) return null;

        return match(getOnlinePlayers(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
