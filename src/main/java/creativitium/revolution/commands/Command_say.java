package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "say",
        description = "Broadcast a message to the server.",
        usage = "/say <message...>",
        aliases = {"broadcast", "bcast"},
        permission = "revolution.command.say",
        source = SourceType.BOTH)
public class Command_say extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        broadcast("revolution.command.say.broadcast", Placeholder.unparsed("name", sender.getName()), Placeholder.parsed("message", StringUtils.join(args, " ")));
        return true;
    }
}
