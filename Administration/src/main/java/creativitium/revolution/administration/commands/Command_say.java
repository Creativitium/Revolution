package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.RCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Command_say extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        broadcast("administration.command.say.broadcast",
                Placeholder.unparsed("name", sender.getName()),
                Placeholder.parsed("message", StringUtils.join(args, " ")));
        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
