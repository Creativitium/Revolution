package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "stop",
        description = "Disconnects all players from the server and shuts it down.",
        usage = "/stop",
        aliases = {"shutdown"},
        permission = "revolution.command.stop",
        source = SourceType.BOTH
)
public class Command_stop extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        plugin.gua.shutdown();
        return true;
    }
}
