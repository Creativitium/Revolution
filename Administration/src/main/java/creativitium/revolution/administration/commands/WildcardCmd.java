package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "wildcard",
        description = "Run a command for every player that is on the server.",
        usage = "/wildcard <command>",
        permission = "administration.command.wildcard",
        source = SourceType.BOTH)
public class WildcardCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        if (getPlugin().getConfig().getStringList("wildcard.blacklist").contains(args[0].toLowerCase()))
        {
            msg(sender, "administration.command.wildcard.blacklisted");
            return true;
        }

        final String command = String.join(" ", args);
        final String placeholder = getPlugin().getConfig().getString("wildcard.placeholder", "@p");

        msg(sender, "administration.command.wildcard.executing", Placeholder.unparsed("command", "/" + command));
        Bukkit.getOnlinePlayers().forEach(player ->
        {
            if (SourceType.ONLY_IN_GAME.matchesSourceType(sender))
                playerSender.performCommand(command.replaceAll(placeholder, player.getName()));
            else
                Bukkit.dispatchCommand(sender, command.replaceAll(placeholder, player.getName()));
        });
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
