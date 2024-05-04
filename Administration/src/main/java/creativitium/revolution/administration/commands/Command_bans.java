package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.services.BanService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "bans",
        description = "Manage bans",
        usage = "/bans <count | reload>",
        permission = "administration.command.bans",
        source = SourceType.BOTH)
public class Command_bans extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        final BanService service = Administration.getInstance().getBanService();

        switch (args[0].toLowerCase())
        {
            case "reload" ->
            {
                service.getBans().clear();
                service.load();

                msg(sender, "administration.command.bans.reloaded");
            }

            case "count" -> msg(sender, "administration.command.bans.count", Placeholder.unparsed("count", String.valueOf(service.getBans().size())));

            default ->
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1) return null;

        return match(List.of("count", "reload"), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
