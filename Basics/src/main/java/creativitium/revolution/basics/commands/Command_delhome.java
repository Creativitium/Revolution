package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "delhome",
        description = "Deletes a home of yours or someone else's.",
        usage = "/delhome <home | *>",
        permission = "basics.command.delhome",
        source = SourceType.BOTH)
public class Command_delhome extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("*"))
        {
            if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            ((BPlayer) (Shortcuts.getService(Key.key("basics", "primary"))).getPlayerData(playerSender.getUniqueId())).getHomes().clear();
            msg(sender, "basics.command.delhome.deleted.all");
        }
        else if (args[0].contains(":"))
        {
            if (!sender.hasPermission("basics.command.delhome.others"))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            Optional<BPlayer> player;
            String[] split = args[0].split(":");
            if (split.length == 0)
            {
                msg(sender, "basics.command.delhome.others.how_to");
                return true;
            }
            else if (split.length == 1)
            {
                player = (Optional<BPlayer>) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(split[0]);
                player.ifPresentOrElse(playerData ->
                                msg(sender, "basics.command.home.others.list",
                                        Placeholder.unparsed("name", playerData.getName()),
                                        Placeholder.unparsed("homes", StringUtils.join(playerData.getHomes().keySet(), ", "))),
                        () -> msg(sender, "basics.command.error.player_not_found"));
            }
            else
            {
                player = (Optional<BPlayer>) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(split[0]);
                player.ifPresentOrElse(playerData ->
                {
                    if (split[1].equalsIgnoreCase("*"))
                    {
                        playerData.getHomes().clear();
                        msg(sender, "basics.command.delhome.deleted.all.others",
                                Placeholder.unparsed("name", playerData.getName()));
                    }
                    else if (!playerData.getHomes().containsKey(split[1]))
                    {
                        msg(sender, "basics.command.delhome.home_not_found");
                    }
                    else
                    {
                        playerData.getHomes().remove(split[1]);
                        msg(sender, "basics.command.delhome.deleted.others",
                                Placeholder.unparsed("name", playerData.getName()),
                                Placeholder.unparsed("home", split[1]));
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
        }
        else
        {
            if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
            {
                msg(sender, "revolution.command.error.no_permission.subcommand");
                return true;
            }

            BPlayer data = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(playerSender.getUniqueId());
            if (data.getHomes().containsKey(args[0]))
            {
                msg(sender, "basics.command.delhome.home_not_found");
            }
            else
            {
                data.getHomes().remove(args[0]);
                msg(sender, "basics.command.delhome.delete", Placeholder.unparsed("home", args[0]));
            }

            return true;

        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        List<String> response = new ArrayList<>();

        if (args.length < 2)
        {
            if (args[0].contains(":") && sender.hasPermission("basics.command.home.others"))
            {
                String[] split = args[0].split(":");
                if (split.length == 0) return null;
                ((Optional<BPlayer>) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(split[0]))
                        .ifPresent(playerData -> response.addAll(playerData.getHomes().keySet().stream()
                                .map(homeName -> String.format("%s:%s", playerData.getName(), homeName)).toList()));
            }

            if (sender instanceof Player player)
            {
                response.addAll(((BPlayer) (Shortcuts.getService(Key.key("basics", "primary")))
                        .getPlayerData(player.getUniqueId())).getHomes().keySet().stream().toList());
                response.add("*");
            }
        }

        return response;
    }
}
