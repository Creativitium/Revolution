package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "spawn",
        description = "Teleport to the spawnpoint.",
        usage = "/spawn [player]",
        permission = "basics.command.spawn",
        source = SourceType.BOTH)
public class SpawnCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        if (args.length > 0 && !sender.hasPermission("basics.command.spawn.others"))
        {
            msg(sender, "basics.command.spawn.no_permission");
            return true;
        }

        final Location spawnpoint = Basics.getInstance().getBasicsService().getSpawnpoint();
        if (spawnpoint == null)
        {
            msg(sender, "basics.command.spawn.no_spawn_set");
            return true;
        }

        final Optional<Player> target = args.length == 0 ? Optional.of(playerSender) : getPlayer(args[0]);
        target.ifPresentOrElse(player ->
        {
           if (player != playerSender)
           {
               msg(sender, "basics.command.spawn.teleporting_other", Placeholder.unparsed("player", player.getName()));
               msg(player, "basics.command.spawn.sent_to_spawn", Placeholder.unparsed("player", sender.getName()));
           }
           else
           {
               msg(player, "basics.command.spawn.teleporting");
           }

           player.teleportAsync(spawnpoint, PlayerTeleportEvent.TeleportCause.COMMAND);
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1 || !sender.hasPermission("basics.command.spawn.others")) return null;

        return match(getOnlinePlayers(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
