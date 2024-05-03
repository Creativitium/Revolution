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

@CommandParameters(name = "teleport",
        description = "Teleport to a player or teleport them to another player.",
        usage = "/teleport <player> [player2]",
        aliases = {"tp"},
        permission = "basics.command.teleport",
        source = SourceType.BOTH)
public class Command_teleport extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length == 1 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        if (args.length >= 2 && !sender.hasPermission("basics.command.teleport.others"))
        {
            msg(sender, "basics.command.teleport.no_permission_others");
            return true;
        }

        // This is a pretty bad way of doing things, but it will work for now
        final Optional<Player> teleporter = args.length == 1 ? Optional.of(playerSender) : getPlayer(args[0]);
        final Optional<Player> target = args.length == 1 ? getPlayer(args[0]) : getPlayer(args[1]);

        if (teleporter.isEmpty() || target.isEmpty())
        {
            msg(sender, "revolution.command.error.player_not_found");
            return true;
        }

        msg(teleporter.get(), "basics.command.teleport.teleporting", Placeholder.unparsed("player", target.get().getName()));
        teleporter.get().teleportAsync(target.get().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        // We're not fucking around
        if (args.length == 0 || args.length > 3 || args.length == 1 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)
                || args.length == 2 && !sender.hasPermission("basics.command.teleport.others")) return null;

        return match(getOnlinePlayers(), args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
