package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@CommandParameters(name = "near",
        description = "Gets a list of players that are near you.",
        usage = "/near [radius]",
        aliases = {"radar"},
        permission = "basics.command.near",
        source = SourceType.ONLY_IN_GAME)
public class NearCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        int radius;
        try
        {
            radius = Math.abs(args.length == 0 ? 100 : Integer.parseInt(args[0]));
        }
        catch (NumberFormatException ex)
        {
            msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[0]));
            return true;
        }

        // Lower limit
        if (!sender.hasPermission("basics.command.near.bypass_cap"))
        {
            radius = Math.min(250, radius);
        }

        // Upper limit, anything higher will likely crash the server
        radius = Math.min(radius, 50000);

        final Location location = playerSender.getLocation();
        final List<Component> players = location.getNearbyPlayers(radius).stream().filter(player -> playerSender != player).map(player ->
                getMessage("basics.command.near.nearby_players.format",
                        Placeholder.parsed("name", player.getName()),
                        Placeholder.unparsed("distance", String.valueOf((int) player.getLocation().distance(location))),
                        Placeholder.component("display_name", player.displayName()))).toList();

        if (players.isEmpty())
        {
            msg(sender, "basics.command.near.nobody_is_nearby", Placeholder.unparsed("radius", String.valueOf(radius)));
        }
        else
        {
            msg(sender, "basics.command.near.nearby_players", Placeholder.component("players", Component.join(JoinConfiguration.commas(true), players)));
        }
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
