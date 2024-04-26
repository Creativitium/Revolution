package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandParameters(name = "stop",
        description = "Disconnects all players from the server and shuts it down.",
        usage = "/stop",
        aliases = {"shutdown"},
        permission = "administration.command.stop",
        source = SourceType.BOTH)
public class Command_stop extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(Foundation.getInstance().getMessageService().getMessage("administration.shutdown.message")));
        Bukkit.shutdown();
        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
