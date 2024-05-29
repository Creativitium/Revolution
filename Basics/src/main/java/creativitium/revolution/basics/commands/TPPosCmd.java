package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
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

@CommandParameters(name = "tppos",
        description = "Teleport to a set of coordinates!",
        usage = "/tppos <x> <y> <z> [world]",
        permission = "basics.command.tppos",
        source = SourceType.ONLY_IN_GAME)
public class TPPosCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 3)
        {
            return false;
        }

        int x;
        int y;
        int z;
        World world;

        try
        {
            x = !args[0].equalsIgnoreCase("~") ? Integer.parseInt(args[0]) : playerSender.getLocation().blockX();
            y = !args[1].equalsIgnoreCase("~") ? Integer.parseInt(args[1]) : playerSender.getLocation().blockY();
            z = !args[2].equalsIgnoreCase("~") ? Integer.parseInt(args[2]) : playerSender.getLocation().blockZ();
        }
        catch (Exception ex)
        {
            msg(sender, "revolution.command.error.invalid_number_unknown");
            return true;
        }

        if (Math.abs(x) >= 29999999 || Math.abs(y) >= 29999999 || Math.abs(z) >= 29999999)
        {
            msg(sender, "basics.command.tppos.out_of_this_world");
            return true;
        }

        world = args.length > 3 ? Bukkit.getWorld(args[3]) : playerSender.getWorld();

        if (world == null)
        {
            msg(sender, "basics.command.world.world_not_found");
            return true;
        }

        msg(sender, "basics.command.tppos.teleporting");
        playerSender.teleportAsync(new Location(world, x, y, z), PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length > 4) return null;

        return match(args.length == 4 ? Bukkit.getWorlds().stream().map(World::getName).toList() : List.of("~"), args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
