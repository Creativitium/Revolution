package creativitium.revolution.commands;

import creativitium.revolution.players.PlayerData;
import creativitium.revolution.templates.RCommand;
import creativitium.revolution.utilities.MM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandParameters(name = "nickname",
        description = "Change your nickname.",
        usage = "/nickname <[nickname] | off [player] | clearall>",
        aliases = {"nick"},
        permission = "revolution.command.nickname",
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

                if (args.length > 1 && sender.hasPermission("revolution.command.nickname.clear_others"))
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
                    plugin.pls.getPlayerData(player.getUniqueId()).setNickname(null);
                    msg((player.getName().equalsIgnoreCase(sender.getName()) ? sender : player), "revolution.command.nickname.cleared");
                    if (!player.getName().equalsIgnoreCase(sender.getName()))
                    {
                        msg(sender, "revolution.command.nickname.cleared.other", Placeholder.unparsed("name", player.getName()));
                    }
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            case "clearall" ->
            {
                if (!sender.hasPermission("revolution.command.nickname.clear_all"))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                action(sender, plugin.msg.getMessage("revolution.action.nickname.clearing_all", new TagResolver[0]));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    plugin.pls.getPlayerData(player.getUniqueId()).setNickname(null);
                    msg(player, "revolution.command.nickname.cleared");
                });
            }
            default ->
            {
                if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }

                PlayerData data = plugin.pls.getPlayerData(playerSender.getUniqueId());
                Matcher useLegacy = AMPERSAND_PATTERN.matcher(args[0]);
                Component nickname = useLegacy.find() ? LegacyComponentSerializer.legacyAmpersand().deserialize(args[0].replaceAll("(&(?i)k)", "")) : MM.getNonExploitable().deserialize(args[0]);


                // Some verification first
                String plainText = PlainTextComponentSerializer.plainText().serialize(nickname).trim();
                Optional<PlayerData> lol = plugin.pls.getAllPlayerData().stream().filter(player -> !sender.getName().equalsIgnoreCase(player.getName()))
                        .filter(player -> plainText.equalsIgnoreCase(player.getName()) || PlainTextComponentSerializer.plainText().serialize(player.getNickname()).equalsIgnoreCase(plainText)).findAny();
                if (lol.isPresent())
                {
                    msg(sender, "revolution.command.nickname.another_player_has_that_nickname");
                    return true;
                }

                data.setNickname(nickname);
                msg(sender, "revolution.command.nickname.set", Placeholder.component("nickname", data.getNickname()));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        List<String> options = new ArrayList<>();

        if (args.length <= 1)
        {
            if (SourceType.ONLY_IN_GAME.matchesSourceType(sender)) options.add(sender.getName());
            options.add("off");
            if (sender.hasPermission("revolution.command.nickname.clear_all")) options.add("clearall");
        }
        else
        {
            if (sender.hasPermission("revolution.command.nickname.clear_others"))
            {
                options.addAll(getOnlinePlayers());
            }
        }

        return options;
    }
}
