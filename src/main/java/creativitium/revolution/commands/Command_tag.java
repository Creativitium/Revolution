package creativitium.revolution.commands;

import creativitium.revolution.players.PlayerData;
import creativitium.revolution.templates.RCommand;
import creativitium.revolution.utilities.MM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandParameters(name = "tag",
        description = "Manage your tag.",
        usage = "/tag <set [tag...] | clear [player] | clearall>",
        aliases = {"prefix"},
        permission = "revolution.command.tag",
        source = SourceType.BOTH)
public class Command_tag extends RCommand
{
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("(&(?i)[a-fklmnor\\d]{1})");

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "set" ->
            {
                if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                PlayerData data = plugin.pls.getPlayerData(playerSender.getUniqueId());

                String tag = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
                Matcher useLegacy = AMPERSAND_PATTERN.matcher(tag);
                data.setTag(useLegacy.find() ? LegacyComponentSerializer.legacyAmpersand().deserialize(tag) : MM.getLessExploitable().deserialize(tag));

                msg(sender, "revolution.command.tag.set", Placeholder.component("tag", data.getTag()));
            }
            case "clear" ->
            {
                Optional<Player> target;

                if (args.length >= 2)
                {
                    if (!sender.hasPermission("revolution.command.tag.clear_others"))
                    {
                        msg(sender, "revolution.command.error.no_permission.subcommand");
                        return true;
                    }

                    target = Optional.ofNullable(Bukkit.getPlayer(args[1]));
                }
                else
                {
                    if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
                    {
                        msg(sender, "revolution.command.error.no_permission.subcommand");
                        return true;
                    }

                    target = Optional.of(playerSender);
                }

                target.ifPresentOrElse(player -> {
                    plugin.pls.getPlayerData(player.getUniqueId()).setTag(Component.empty());
                    msg((player.getName().equalsIgnoreCase(sender.getName()) ? sender : player), "revolution.command.tag.cleared");
                    if (!player.getName().equalsIgnoreCase(sender.getName()))
                    {
                        msg(sender, "revolution.command.tag.cleared.other", Placeholder.unparsed("name", player.getName()));
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            case "clearall" ->
            {
                if (!sender.hasPermission("revolution.command.tag.clear_all"))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                action(sender, plugin.msg.getMessage("revolution.action.tag.clearing_all", new TagResolver[0]));
                Bukkit.getOnlinePlayers().forEach(player ->
                {
                    plugin.pls.getPlayerData(player.getUniqueId()).setTag(Component.empty());
                    msg(player, "revolution.command.tag.cleared");
                });
            }
            default ->
            {
                return false;
            }
        }

        return true;
    }
}
