package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.BiOptional;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
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
public class TeleportCmd extends RCommand
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

        final BiOptional<Player, Player> targets = BiOptional.fromOptionals(
                args.length == 1 ? Optional.of(playerSender) : getPlayer(args[0]),
                args.length == 1 ? getPlayer(args[0]) : getPlayer(args[1]));

        if (targets.areBothEqual() && targets.areBothPresent())
        {
            msg(sender, "basics.command.teleport.cannot_teleport_to_" + (targets.getLeft() == playerSender ? "yourself" : "themselves"));
            return true;
        }

        // Definitely an ugly hack, but whatever
        if (targets.areBothPresent())
        {
            if (targets.leftMeetsConditions(player -> player != playerSender &&
                    !((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).isTpEnabled()))
            {
                msg(sender, "basics.command.teleport.player_has_tp_disabled", Placeholder.unparsed("player", targets.getLeft().getName()));
                return true;
            }
            else if (targets.rightMeetsConditions(player -> player != playerSender &&
                    !((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).isTpEnabled()))
            {
                msg(sender, "basics.command.teleport.player_has_tp_disabled", Placeholder.unparsed("player", targets.getRight().getName()));
                return true;
            }
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
