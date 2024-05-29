package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "world",
        description = "Teleport to other worlds.",
        usage = "/world <world> [player]",
        permission = "basics.command.world",
        source = SourceType.ONLY_IN_GAME)
public class WorldCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final World world = Bukkit.getWorld(StringUtils.join(args, " "));
        if (world == null)
        {
            msg(sender, "basics.command.world.world_not_found");
            return true;
        }

        final Location playerLocation = playerSender.getLocation();
        msg(sender, "basics.command.world.teleporting");
        playerSender.teleportAsync(new Location(world, playerLocation.x(), playerLocation.y(), playerLocation.z()), PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(Bukkit.getWorlds().stream().map(World::getName).toList(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
