package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.services.BanService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "unban",
        description = "Unban a player.",
        usage = "/unban <player>",
        permission = "administration.command.unban",
        source = SourceType.BOTH)
public class Command_unban extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final OfflinePlayer player = getOfflinePlayer(args[0]);
        final BanService bs = Administration.getInstance().getBanService();

        bs.getEntryByOfflinePlayer(player).ifPresentOrElse(entry ->
        {
            action(sender, "administration.action.unban", Placeholder.unparsed("player", entry.getUsername()));
            bs.removeEntry(entry.getUuid());
        }, () -> msg(sender, "administration.command.unban.not_banned"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(Administration.getInstance().getBanService().getBannedNames(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
