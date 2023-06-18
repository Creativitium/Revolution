package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "kick",
        description = "Kicks a player from the server.",
        usage = "/kick <player> [reason]",
        aliases = "eject",
        permission = "revolution.command.kick",
        source = SourceType.BOTH
)
public class Command_kick extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        getPlayer(args[0]).ifPresentOrElse((player) -> plugin.sen.kickPlayer(player, sender, StringUtils.join(ArrayUtils.remove(args, 0), " ")),
                () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }
}
