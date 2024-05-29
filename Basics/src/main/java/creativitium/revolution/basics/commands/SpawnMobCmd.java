package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@CommandParameters(name = "spawnmob",
        description = "Spawn a mob in.",
        usage = "/spawnmob <type> [amount]",
        aliases = {"mob"},
        permission = "basics.command.spawnmob",
        source = SourceType.ONLY_IN_GAME)
public class SpawnMobCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        int amount;
        final EntityType type;
        try
        {
            amount = args.length >= 2 ? Math.min(getPlugin().getConfig().getInt("spawnmob.limit", 10), Integer.parseInt(args[1])) : 1;
            type = EntityType.valueOf(args[0].toUpperCase());
        }
        catch (NumberFormatException ex)
        {
            msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[1]));
            return true;
        }
        catch (IllegalArgumentException ex)
        {
            msg(sender, "revolution.command.error.invalid_type", Placeholder.unparsed("type", args[0]));
            return true;
        }

        if (type == EntityType.PLAYER)
        {
            msg(sender, "basics.command.spawnmob.player_is_not_a_mob");
            return true;
        }
        else if (!type.isAlive() && !type.isSpawnable())
        {
            msg(sender, "basics.command.spawnmob.not_a_mob");
            return true;
        }

        final Location loc = playerSender.getLocation();
        for (int i = 0; i < amount; i++)
        {
            playerSender.getWorld().spawnEntity(loc, type, CreatureSpawnEvent.SpawnReason.COMMAND);
        }

        msg(sender, "basics.command.spawnmob.spawned", Placeholder.unparsed("amount", String.valueOf(amount)));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length > 2) return null;

        return match(args.length == 1 ? Arrays.stream(EntityType.values()).filter(type -> type.isAlive()
                && type.isSpawnable() && type != EntityType.PLAYER).map(type -> type.name().toLowerCase()).toList()
                : IntStream.range(0, getPlugin().getConfig().getInt("spawnmob.limit", 10))
                .mapToObj(String::valueOf).toList(), args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
