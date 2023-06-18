package creativitium.revolution.commands;

import creativitium.revolution.players.PlayerData;
import creativitium.revolution.templates.RCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "home",
        description = "Teleports you to one of your homes.",
        usage = "/home <home>",
        aliases = {"homes"},
        permission = "revolution.command.home",
        source = SourceType.ONLY_IN_GAME)
public class Command_home extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final PlayerData data = plugin.pls.getPlayerData(playerSender.getUniqueId());

        if (args.length == 0)
        {
            msg(sender, "revolution.command.home.list", Placeholder.unparsed("homes", StringUtils.join(data.getHomes().keySet(), ", ")));
            return true;
        }

        if (args[0].contains(":"))
        {
            if (!sender.hasPermission("revolution.command.home.others"))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            Optional<PlayerData> player;
            String[] split = args[0].split(":");
            if (split.length == 0)
            {
                msg(sender, "revolution.command.home.others.how_to");
                return true;
            }
            else if (split.length == 1)
            {
                player = plugin.pls.getPlayerData(split[0]);
                player.ifPresentOrElse(playerData ->
                        msg(sender, "revolution.command.home.others.list",
                                Placeholder.unparsed("name", playerData.getName()),
                                Placeholder.unparsed("homes", StringUtils.join(playerData.getHomes().keySet(), ", "))),
                        () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            else
            {
                player = plugin.pls.getPlayerData(split[0]);
                player.ifPresentOrElse(playerData ->
                {
                    Location loc = playerData.getHomes().get(split[1]);
                    if (loc == null)
                    {
                        msg(sender, "revolution.command.home.invalid_home", Placeholder.unparsed("home", split[1]));
                    }
                    else
                    {
                        msg(sender, "revolution.command.home.teleporting");
                        playerSender.teleportAsync(loc);
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }

            plugin.pls.getPlayerData(split[0]);
        }
        else
        {
            if (data.getHomes().containsKey(args[0]))
            {
                msg(sender, "revolution.command.home.teleporting");
                playerSender.teleportAsync(data.getHomes().get(args[0]));
            }
            else
            {
                msg(sender, "revolution.command.home.invalid_home", Placeholder.unparsed("home", args[0]));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length > 0 && sender instanceof Player player)
        {
            if (args[0].contains(":") && sender.hasPermission("revolution.command.home.others"))
            {
                String[] split = args[0].split(":");
                if (split.length == 0) return null;
                Optional<PlayerData> data = plugin.pls.getPlayerData(split[0]);

                if (data.isPresent())
                {
                    return data.get().getHomes().keySet().stream().map(homeName -> String.format("%s:%s", data.get().getName(), homeName)).toList();
                }
            }
            else
            {
                return plugin.pls.getPlayerData(player.getUniqueId()).getHomes().keySet().stream().toList();
            }
        }

        return null;
    }
}
