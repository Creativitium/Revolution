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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandParameters(name = "nickname",
        description = "Change your nickname.",
        usage = "/nickname <[nickname] | off [player] | clearall>",
        aliases = {"nick"},
        permission = "basics.command.nickname",
        source = SourceType.BOTH)
public class Command_nickname extends RCommand
{
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("(&(?i)[a-fklmnor\\d]{1})");

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        switch (args[0].toLowerCase())
        {
            case "off" ->
            {
                Optional<Player> target;

                if (args.length > 1 && sender.hasPermission("basics.command.nickname.clear_others"))
                {
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
                    ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).setNickname(null);
                    player.displayName(null);
                    msg((player.getName().equalsIgnoreCase(sender.getName()) ? sender : player), "basics.command.nickname.cleared");
                    if (!player.getName().equalsIgnoreCase(sender.getName()))
                    {
                        msg(sender, "basics.command.nickname.cleared.other", Placeholder.unparsed("name", player.getName()));
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            case "clearall" ->
            {
                if (!sender.hasPermission("basics.command.nickname.clear_all"))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                action(sender, "basics.action.nickname.clearing_all");
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).setNickname(null);
                    player.displayName(null);
                    msg(player, "basics.command.nickname.cleared");
                });
            }
            default ->
            {
                if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                BPlayer data = ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(playerSender.getUniqueId()));
                Matcher useLegacy = AMPERSAND_PATTERN.matcher(args[0]);
                Component nickname = useLegacy.find() ? LegacyComponentSerializer.legacyAmpersand().deserialize(args[0].replaceAll("(&(?i)k)", "")) : MM.getNonExploitable().deserialize(args[0]);

                // Some verification first
                String plainText = PlainTextComponentSerializer.plainText().serialize(nickname).trim();
                Optional<BPlayer> lol = ((Collection<BPlayer>) Shortcuts.getService(Key.key("basics", "primary")).getAllPlayerData()).stream().filter(player -> !sender.getName().equalsIgnoreCase(player.getName()))
                        .filter(player -> plainText.equalsIgnoreCase(player.getName()) || (player.getNickname() != null && PlainTextComponentSerializer.plainText().serialize(player.getNickname()).equalsIgnoreCase(plainText))).findAny();
                if (lol.isPresent())
                {
                    msg(sender, "basics.command.nickname.another_player_has_that_nickname");
                    return true;
                }

                if (plainText.length() > 24)
                {
                    msg(sender, "basics.command.nickname.too_long");
                    return true;
                }

                data.setNickname(nickname);
                playerSender.displayName(nickname);
                msg(sender, "basics.command.nickname.set", Placeholder.component("nickname", data.getNickname()));
            }
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
        List<String> options = new ArrayList<>();

        if (args.length <= 1)
        {
            if (SourceType.ONLY_IN_GAME.matchesSourceType(sender)) options.add(sender.getName());
            options.add("off");
            if (sender.hasPermission("basics.command.nickname.clear_all")) options.add("clearall");
        }
        else
        {
            if (sender.hasPermission("basics.command.nickname.clear_others"))
            {
                options.addAll(getOnlinePlayers());
            }
        }

        return options;
    }
}
