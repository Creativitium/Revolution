package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.BiOptional;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "tpo",
        description = "Teleport to a player or teleport them to another player, overriding their teleportation status.",
        usage = "/tpo <player> [player2]",
        aliases = {"teleportoverride"},
        permission = "basics.command.tpo",
        source = SourceType.BOTH)
public class TPOCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length == 1 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        final BiOptional<Player, Player> targets = BiOptional.fromOptionals(
                args.length == 1 ? Optional.of(playerSender) : getPlayer(args[0]),
                args.length == 1 ? getPlayer(args[0]) : getPlayer(args[1]));

        if (targets.areBothEqual() && targets.areBothPresent())
        {
            msg(sender, "basics.command.teleport.cannot_teleport_to_" + (targets.getLeft() == playerSender ? "yourself" : "themselves"));
            return true;
        }

        targets.ifBothPresentOrElse((player, player2) ->
        {
            if (player2 == playerSender)
            {
                msg(sender, "basics.command.teleport.teleporting_to_you", Placeholder.unparsed("player", player.getName()));
            }
            else if (player != playerSender)
            {
                msg(sender, "basics.command.teleport.teleporting_others", Placeholder.unparsed("player", player.getName()),
                        Placeholder.unparsed("player2", player2.getName()));
            }

            msg(player, "basics.command.teleport.teleporting", Placeholder.unparsed("player", player2.getName()));
            player.teleportAsync(player2.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        // We're not fucking around
        if (args.length == 0 || args.length > 3 || args.length == 1 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return null;

        return match(getOnlinePlayers(), args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
