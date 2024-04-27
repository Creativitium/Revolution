package creativitium.revolution.moderation.commands;

import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.moderation.Moderation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandParameters(name = "toggle",
        description = "Disable/adjust certain game mechanics.",
        usage = "/toggle <type> [options]",
        permission = "moderation.command.toggle",
        source = SourceType.BOTH)
public class Command_toggle extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Plugin getPlugin()
    {
        return Moderation.getInstance();
    }
}
