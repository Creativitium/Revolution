package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@CommandParameters(name = "list",
        description = "Get a list of players on the server.",
        usage = "/list",
        permission = "basics.command.list",
        source = SourceType.BOTH)
public class ListCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 0) return false;

        final Collection<? extends Player> list = Bukkit.getOnlinePlayers();

        // Singular list
        if (list.size() == 1)
        {
            msg(sender, "basics.command.list.singular",
                    Placeholder.parsed("amount", String.valueOf(list.size())),
                    Placeholder.parsed("max", String.valueOf(Bukkit.getMaxPlayers())));
        }
        // Plural list
        else
        {
            msg(sender, "basics.command.list.plural",
                    Placeholder.parsed("amount", String.valueOf(list.size())),
                    Placeholder.parsed("max", String.valueOf(Bukkit.getMaxPlayers())));
        }

        if (!list.isEmpty())
        {
            msg(sender, "basics.command.list.list", Placeholder.component("list",
                    Component.join(JoinConfiguration.commas(true), list.stream().map(player ->
                            getMessage("basics.command.list.list.format", Placeholder.parsed("name", player.getName()),
                                    Placeholder.component("prefix", Foundation.getInstance().getVaultHook().getPrefixAsComponent(player)),
                                    Placeholder.component("display", player.displayName()))).toList())));
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
