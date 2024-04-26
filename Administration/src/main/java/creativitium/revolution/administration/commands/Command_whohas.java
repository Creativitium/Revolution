package creativitium.revolution.administration.commands;

import com.destroystokyo.paper.MaterialTags;
import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@CommandParameters(name = "whohas",
        description = "List all the players who have a certain item in their inventory.",
        usage = "/whohas <item>",
        aliases = {"wh"},
        permission = "administration.command.whohas",
        source = SourceType.BOTH)
public class Command_whohas extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        Optional.ofNullable(Material.matchMaterial(args[0].toUpperCase())).ifPresentOrElse(material -> {
            List<? extends Player> players = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getInventory().contains(material)).toList();

            if (players.isEmpty())
            {
                msg(sender, "administration.command.whohas.nobody");
            }
            else
            {
                msg(sender, "administration.command.whohas.somebody",
                        Placeholder.unparsed("item", material.name()),
                        Placeholder.component("players", Component.join(JoinConfiguration.commas(true), players.stream().map(player -> Component.text(player.getName())).toList())));
            }
        }, () -> msg(sender, "revolution.command.error.item_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(Arrays.stream(Material.values()).filter(Material::isItem).map(material -> material.name().toLowerCase()).toList(), args[0]) : null;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
