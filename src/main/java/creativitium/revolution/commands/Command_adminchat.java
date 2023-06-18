package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "adminchat",
        description = "Send a message to the admin chat.",
        usage = "/adminchat <message>",
        aliases = {"ac", "o"},
        permission = "revolution.components.staffchat.admin",
        source = SourceType.BOTH)
public class Command_adminchat extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        plugin.msg.adminChatMessage(sender, StringUtils.join(args, " "));
        return true;
    }
}
