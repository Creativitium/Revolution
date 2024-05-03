package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "fly",
        description = "Toggles your ability to fly.",
        usage = "/fly [player]",
        permission = "basics.command.fly",
        source = SourceType.BOTH)
public class Command_fly extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        if (args.length != 0 && !sender.hasPermission("basics.command.fly.others"))
        {
            msg(sender, "basics.command.fly.no_permission");
            return true;
        }

        final Optional<Player> target = (args.length == 0 ? Optional.of(playerSender) : getPlayer(args[0]));

        target.ifPresentOrElse(player -> {
            player.setAllowFlight(!player.getAllowFlight());

            if (player != playerSender)
            {
                msg(sender, "basics.command.fly.other_" + (player.getAllowFlight() ? "enabled" : "disabled"),
                        Placeholder.unparsed("player", player.getName()));
            }

            msg(player, "basics.command.fly." + (player.getAllowFlight() ? "enabled" : "disabled"));
        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1 || !sender.hasPermission("basics.command.fly.others")) return null;

        return match(getOnlinePlayers(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
