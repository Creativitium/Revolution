package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "invsee",
        description = "Open another player's inventory.",
        usage = "/invsee <player>",
        permission = "administration.command.invsee",
        source = SourceType.ONLY_IN_GAME)
public class Command_invsee extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        getPlayer(args[0]).ifPresentOrElse(player -> playerSender.openInventory(player.getInventory()),
                () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(getOnlinePlayers(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
