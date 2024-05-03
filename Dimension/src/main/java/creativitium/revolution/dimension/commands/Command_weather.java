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

@CommandParameters(name = "weather",
        description = "Control the weather for a world.",
        usage = "/weather <clear | rain | thunder> [world]",
        permission = "dimension.command.weather",
        source = SourceType.BOTH)
public class Command_weather extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length == 1 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        // We use this slightly convoluted way of getting worlds based on the argument length and the sender type.
        final World world = args.length == 1 ? SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? playerSender.getWorld() : null : Bukkit.getWorld(args[1]);

        if (world == null)
        {
            msg(sender, "dimension.command.error.world_not_found");
            return true;
        }

        if (!sender.hasPermission("dimension." + world.getName().toLowerCase().replaceAll(" ", "_") + ".change_weather"))
        {
            msg(sender, "dimension.command.weather.no_permission_weather");
            return true;
        }

        switch (args[0].toLowerCase())
        {
            case "thunder" -> world.setThundering(true);
            case "rain" -> world.setStorm(true);
            case "clear" ->
            {
                world.setStorm(false);
                world.setThundering(false);
            }
            default ->
            {
                msg(sender, "dimension.command.weather.invalid_mode", Placeholder.unparsed("mode", args[0].toLowerCase()));
                return true;
            }
        }

        msg(sender, "dimension.command.weather.set_weather",
                Placeholder.unparsed("world", world.getName()),
                Placeholder.unparsed("mode", args[0].toLowerCase()));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final List<String> options;

        if (args.length == 1)
        {
            options = List.of("clear", "rain", "thunder");
        }
        else if (args.length == 2)
        {
            options = Bukkit.getWorlds().stream().map(World::getName).filter(name -> sender.hasPermission("dimension." + name.toLowerCase().replaceAll(" ", "_")+ ".change_weather")).toList();
        }
        else
        {
            return null;
        }

        return match(options, args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Dimension.getInstance();
    }
}
