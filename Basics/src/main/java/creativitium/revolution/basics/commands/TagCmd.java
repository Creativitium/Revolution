package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.MM;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandParameters(name = "tag",
        description = "Manage your tag.",
        usage = "/tag <set [tag...] | clear [player] | clearall>",
        aliases = {"prefix"},
        permission = "basics.command.tag",
        source = SourceType.BOTH)
public class TagCmd extends RCommand
{
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("(&(?i)[a-fklmnor\\d])");

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

                BPlayer data = ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(playerSender.getUniqueId()));

                String tag = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
                if (tag.length() > 128)
                {
                    msg(sender, "basics.command.tag.too_long_upper");
                    return true;
                }

                Matcher useLegacy = AMPERSAND_PATTERN.matcher(tag);
                Component outcome = useLegacy.find() ? LegacyComponentSerializer.legacyAmpersand().deserialize(tag) : MM.getLessExploitable().deserialize(tag);
                String plainText = PlainTextComponentSerializer.plainText().serialize(outcome).trim();

                if (plainText.length() > 32)
                {
                    msg(sender, "basics.command.tag.too_long_lower");
                    return true;
                }

                data.setTag(outcome);
                msg(sender, "basics.command.tag.set", Placeholder.component("tag", outcome));
            }
            case "clear" ->
            {
                Optional<Player> target;

                if (args.length >= 2)
                {
                    if (!sender.hasPermission("basics.command.tag.clear_others"))
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
                    ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).setTag(null);
                    msg((player.getName().equalsIgnoreCase(sender.getName()) ? sender : player), "basics.command.tag.cleared");
                    if (!player.getName().equalsIgnoreCase(sender.getName()))
                    {
                        msg(sender, "basics.command.tag.cleared.other", Placeholder.unparsed("name", player.getName()));
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            case "clearall" ->
            {
                if (!sender.hasPermission("basics.command.tag.clear_all"))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                action(sender, "basics.action.tag.clearing_all");
                Bukkit.getOnlinePlayers().forEach(player ->
                {
                    ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId()))
                            .setTag(null);
                    msg(player, "basics.command.tag.cleared");
                });
            }
            default ->
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
