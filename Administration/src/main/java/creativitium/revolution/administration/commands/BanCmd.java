package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.Ban;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import creativitium.revolution.foundation.utilities.Util;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CommandParameters(name = "ban",
        description = "Ban a player.",
        usage = "/ban <player> [reason]",
        aliases = {"gtfo"},
        permission = "administration.command.ban",
        source = SourceType.BOTH)
public class BanCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final OfflinePlayer player = getOfflinePlayer(args[0]);
        if (!player.hasPlayedBefore())
        {
            msg(sender, "revolution.command.error.player_not_found");
            return true;
        }

        final String reason = args.length > 1 ? StringUtils.join(ArrayUtils.remove(args, 0), " ") : null;
        final List<String> ips = new ArrayList<>();

        if (player.isOnline())
        {
            ips.add(Objects.requireNonNull(Objects.requireNonNull(player.getPlayer()).getAddress()).getAddress().getHostAddress());
        }
        else
        {
            if (Bukkit.getPluginManager().isPluginEnabled("Basics"))
            {
                final BPlayer bPlayer = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId());
                if (bPlayer.getLastIP() != null)
                {
                    ips.add(bPlayer.getLastIP().trim());
                }
            }
        }

        final Ban ban = Ban.builder()
                .username(player.getName())
                .uuid(player.getUniqueId())
                .ips(ips)
                .issued(Instant.now().getEpochSecond())
                .reason(reason)
                .by(sender.getName())
                .byUuid(SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? playerSender.getUniqueId() : null)
                .expires(Util.getOffsetFromCurrentTime(24, Util.TimeUnit.HOURS)).build();

        Administration.getInstance().getBanService().addEntry(ban);

        action(sender, "administration.action.ban", Placeholder.unparsed("player", player.getName() != null ? player.getName() : args[0]),
                Placeholder.component("reason", reason != null ? getMessage("administration.action.ban.reason", Placeholder.unparsed("reason", reason)) : Component.empty()));

        if (player.isOnline())
        {
            Objects.requireNonNull(player.getPlayer()).kick(ban.craftBanMessage());
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(getOnlinePlayers(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
