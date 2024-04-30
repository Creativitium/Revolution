package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CommandParameters(name = "entitywipe",
        description = "Remove entities of a specific type from the server.",
        usage = "/entitywipe [mobs | all | types...]",
        aliases = {"ew", "mp", "mobpurge", "killall", "rd"},
        permission = "administration.command.entitywipe",
        source = SourceType.BOTH)
public class Command_entitywipe extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final List<EntityType> types = new ArrayList<>();

        if (args.length == 0)
        {
            types.addAll(Arrays.stream(EntityType.values()).filter(type -> !type.isAlive()).toList());
        }
        else
        {
            switch (args[0].toLowerCase())
            {
                case "mobs" ->
                {
                    types.addAll(Arrays.stream(EntityType.values()).filter(EntityType::isAlive).toList());
                }
                case "all" ->
                {
                    types.addAll(Arrays.stream(EntityType.values()).toList());
                }
                default ->
                {
                    types.addAll(Arrays.stream(args).filter(type -> {
                        try
                        {
                            EntityType.valueOf(type.toUpperCase());
                            return true;
                        }
                        catch (Throwable ex)
                        {
                            return false;
                        }
                    }).map(type -> EntityType.valueOf(type.toUpperCase())).toList());
                }
            }
        }
        types.removeIf(type -> type == EntityType.PLAYER || getPlugin().getConfig().getStringList("entitywipe.blacklists.entities").contains(type.name()));

        final List<World> worlds = Bukkit.getWorlds().stream().filter(world ->
                !getPlugin().getConfig().getStringList("entitywipe.blacklists.worlds").contains(world.getName())).toList();

        action(sender, "administration.action.entitywipe", Placeholder.unparsed("types", String.valueOf(types.size())),
                Placeholder.unparsed("worlds", String.valueOf(worlds.size())));

        final AtomicInteger affected = new AtomicInteger();
        worlds.forEach(world -> world.getEntities().stream().filter(entity -> types.contains(entity.getType())).forEach(entity ->
        {
            affected.getAndIncrement();
            entity.remove();
        }));

        msg(sender, "administration.command.entitywipe.done", Placeholder.unparsed("count", String.valueOf(affected.get())));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return null;
        final List<String> results = new ArrayList<>();

        if (args.length == 1)
        {
            results.add("all");
            results.add("mobs");
        }
        else
        {
            if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("mobs")) return null;
        }

        results.addAll(Arrays.stream(EntityType.values()).map(type -> type.name().toLowerCase()).toList());
        return match(results, args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
