package creativitium.revolution.administration.commands;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.administration.Administration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandParameters(name = "adminchat",
        description = "Send a message to the admin chat.",
        usage = "/adminchat <message>",
        aliases = {"ac", "o"},
        permission = "administration.components.staffchat",
        source = SourceType.BOTH)
public class Command_adminchat extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        Component sm = Foundation.getInstance().getMessageService().getMessage("administration.components.staffchat",
                Placeholder.unparsed("name", sender.getName()),
                Placeholder.unparsed("message", StringUtils.join(args, " ")));

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("administration.components.staffchat"))
                .forEach(player -> player.sendMessage(sm));
        getPlugin().getComponentLogger().info(sm);

        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
