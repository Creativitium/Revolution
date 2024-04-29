package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CommandParameters(name = "home",
        description = "Teleports you to one of your homes.",
        usage = "/home <home>",
        aliases = {"homes"},
        permission = "basics.command.home",
        source = SourceType.ONLY_IN_GAME)
public class Command_home extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(playerSender.getUniqueId());

        if (args.length == 0)
        {
            final Map<String, Location> homes = data.getHomes();

            if (homes.isEmpty())
            {
                msg(sender, "basics.command.home.no_homes");
            }
            else if (homes.size() == 1)
            {
                msg(sender, "basics.command.home.teleporting");
                playerSender.teleportAsync(homes.values().iterator().next(), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
            else
            {
                msg(sender, "basics.command.home.list", Placeholder.unparsed("homes", StringUtils.join(homes.keySet(), ", ")));
            }
            return true;
        }

        if (args[0].contains(":"))
        {
            if (!sender.hasPermission("basics.command.home.others"))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            Optional<BPlayer> player;
            String[] split = args[0].split(":");
            if (split.length == 0)
            {
                msg(sender, "basics.command.home.others.how_to");
                return true;
            }
            else if (split.length == 1)
            {
                player = (Optional<BPlayer>) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(split[0]);
                player.ifPresentOrElse(playerData ->
                        msg(sender, "basics.command.home.others.list",
                                Placeholder.unparsed("name", playerData.getName()),
                                Placeholder.unparsed("homes", StringUtils.join(playerData.getHomes().keySet(), ", "))),
                        () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            else
            {
                player = (Optional<BPlayer>) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(split[0]);
                player.ifPresentOrElse(playerData ->
                {
                    Location loc = playerData.getHomes().get(split[1]);
                    if (loc == null)
                    {
                        msg(sender, "basics.command.home.invalid_home", Placeholder.unparsed("home", split[1]));
                    }
                    else
                    {
                        msg(sender, "basics.command.home.teleporting");
                        playerSender.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
        }
        else
        {
            if (data.getHomes().containsKey(args[0]))
            {
                msg(sender, "basics.command.home.teleporting");
                playerSender.teleportAsync(data.getHomes().get(args[0]), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
            else
            {
                msg(sender, "basics.command.home.invalid_home", Placeholder.unparsed("home", args[0]));
            }
        }

        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Basics.getInstance();
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 2 && sender instanceof Player player)
        {
            if (args[0].contains(":") && sender.hasPermission("basics.command.home.others"))
            {
                String[] split = args[0].split(":");
                if (split.length == 0) return null;
                Optional<BPlayer> data = (Optional<BPlayer>) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(split[0]);;

                if (data.isPresent())
                {
                    return data.get().getHomes().keySet().stream().map(homeName -> String.format("%s:%s",
                            data.get().getName(), homeName)).toList();
                }
            }
            else
            {
                return ((BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(player.getUniqueId()))
                        .getHomes().keySet().stream().toList();
            }
        }

        return null;
    }
}
