package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "say",
        description = "Broadcast a message to the server.",
        usage = "/say <message...>",
        aliases = {"broadcast", "bcast"},
        permission = "administration.command.say",
        source = SourceType.BOTH)
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
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
