package creativitium.revolution.dimension.commands;

import creativitium.revolution.dimension.Dimension;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "time",
        description = "Set the time for a world.",
        usage = "/time <set | add> <morning | day | noon | sunset | night | ticks> [world]",
        permission = "dimension.command.time",
        source = SourceType.BOTH)
public class TimeCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 2) return false;
        final World world = args.length == 2 ? SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? playerSender.getWorld() : Bukkit.getWorlds().get(0) : Bukkit.getWorld(args[2]);

        if (world == null)
        {
            msg(sender, "dimension.command.error.world_not_found");
            return true;
        }
        else if (!sender.hasPermission("dimension." + world.getName().toLowerCase().replaceAll(" ", "_") + ".change_time"))
        {
            msg(sender, "dimension.command.time.no_permission_time");
            return true;
        }

        // Time value
        long time;

        try
        {
            time = switch (args[1].toLowerCase())
            {
                case "morning" -> 0;
                case "day" -> 1000;
                case "noon" -> 6000;
                case "sunset" -> 12000;
                case "night" -> 13000;
                default -> Long.parseLong(args[1]);
            };
        }
        catch (NumberFormatException ex)
        {
            msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[1]));
            return true;
        }

        switch (args[0].toLowerCase())
        {
            case "set" ->
            {
                if (time < 0)
                {
                    msg(sender, "dimension.command.time.set_negative_time");
                    return true;
                }

                world.setTime(time);
                msg(sender, "dimension.command.time.time_set",
                        Placeholder.unparsed("world", world.getName()),
                        Placeholder.unparsed("time", String.valueOf(time)));
            }
            case "add" ->
            {
                if (time < 0)
                {
                    msg(sender, "dimension.command.time.add_negative_time");
                    return true;
                }

                world.setTime(world.getTime() + time);
                msg(sender, "dimension.command.time.time_added",
                        Placeholder.unparsed("world", world.getName()),
                        Placeholder.unparsed("time", String.valueOf(time)));
            }
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
        if (args.length == 0 || args.length > 3) return null;

        final List<String> options;

        if (args.length == 1)
        {
            options = List.of("set", "add");
        }
        else if (args.length == 2)
        {
            options = List.of("morning", "day", "noon", "sunset", "night");
        }
        else
        {
            options = Bukkit.getWorlds().stream().map(World::getName).filter(world -> sender.hasPermission("dimension." + world.toLowerCase().replaceAll(" ", "_") + ".change_time")).toList();
        }

        return match(options, args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Dimension.getInstance();
    }
}
