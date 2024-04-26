package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "kill",
        description = "Kills yourself or someone else.",
        usage = "/kill [player]",
        permission = "basics.command.kill",
        source = SourceType.BOTH)
public class Command_kill extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final Optional<Player> target;

        if (args.length == 0)
        {
            if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            target = Optional.of(playerSender);
        }
        else
        {
            if (!sender.hasPermission("basics.command.kill.others"))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            target = getPlayer(args[0]);
        }

        target.ifPresentOrElse(player -> player.setHealth(0.0), () ->
                msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Basics.getInstance();
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 && sender.hasPermission("basics.command.kill.others") ? match(getOnlinePlayers(), args[0]) : null;
    }
}
