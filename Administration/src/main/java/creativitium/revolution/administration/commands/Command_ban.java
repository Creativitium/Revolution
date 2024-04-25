package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.Ban;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@CommandParameters(name = "ban",
        description = "Ban a player.",
        usage = "/ban <player> [reason]",
        aliases = {"gtfo"},
        permission = "administration.command.ban",
        source = SourceType.BOTH)
public class Command_ban extends RCommand
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

        final Ban ban = Ban.builder()
                .username(player.getName())
                .uuid(player.getUniqueId())
                .ips(player.isOnline() ? List.of(player.getPlayer().getAddress().getAddress().getHostAddress()) : new ArrayList<>())
                .issued(Instant.now().getEpochSecond())
                .reason(reason)
                .by(sender.getName())
                .byUuid(SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? playerSender.getUniqueId() : null)
                .expires(Util.getOffsetFromCurrentTime(24, Util.TimeUnit.HOURS)).build();

        Administration.getInstance().getBanService().addEntry(ban);

        action(sender, "administration.action.ban", Placeholder.unparsed("player", player.getName()),
                Placeholder.component("reason", reason != null ? Foundation.getInstance().getMessageService().getMessage("administration.action.ban.reason", Placeholder.unparsed("reason", reason)) : Component.empty()));

        if (player.isOnline())
        {
            player.getPlayer().kick(ban.craftBanMessage());
        }

        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
