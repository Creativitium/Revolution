package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "unban",
        description = "Unban a player",
        usage = "/unban <player>",
        permission = "revolution.command.unban",
        source = SourceType.BOTH)
public class Command_unban extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        plugin.sen.getBan(Bukkit.getOfflinePlayer(args[0])).ifPresentOrElse(entry ->
        {
            action(sender, plugin.msg.getMessage("revolution.action.unban", Placeholder.unparsed("player", entry.getValue().getUsername())));
            plugin.sen.removeBan(entry.getKey(), entry.getValue());
        }, () -> msg(sender, "revolution.command.unban.not_banned"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return getOnlinePlayers();
    }
}
