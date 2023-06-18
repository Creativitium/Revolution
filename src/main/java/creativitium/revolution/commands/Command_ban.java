package creativitium.revolution.commands;

import creativitium.revolution.sentinel.Ban;
import creativitium.revolution.templates.RCommand;
import creativitium.revolution.utilities.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

@CommandParameters(name = "ban",
        description = "Bans a player from the server.",
        usage = "/ban <player> [reason]",
        aliases = {"tempban"},
        permission = "revolution.command.ban",
        source = SourceType.BOTH)
public class Command_ban extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        final OfflinePlayer player = getOfflinePlayer(args[0]);

        // This player has never been on the server, so we probably shouldn't waste our time
        if (!player.hasPlayedBefore())
        {
            msg(sender, "revolution.command.error.player_not_found");
            return true;
        }

        // Get the reason
        final String reason = args.length > 1 ? StringUtils.join(ArrayUtils.remove(args, 0), " ") : null;

        // Build the ban
        final Ban ban = Ban.builder().uuid(player.getUniqueId())
                .username(player.getName())
                .reason(reason)
                .by(sender.getName())
                .expires(TimeUtil.getOffsetFromCurrentTime(24, TimeUtil.TimeUnit.HOURS))
                .ips(player.isOnline() ? new ArrayList<>(Collections.singleton(((Player) player).getAddress().getAddress().getHostAddress())) : new ArrayList<>())
                .build();

        // Broadcasts and adds the ban (which also kicks the player)
        action(sender, plugin.msg.getMessage("revolution.action.ban",
                Placeholder.unparsed("player", player.getName()),
                Placeholder.component("reason", reason == null ? Component.empty() :
                        plugin.msg.getMessage("revolution.action.ban.reason", Placeholder.unparsed("reason", reason)))));
        plugin.sen.addBan(player.getUniqueId(), ban);
        return true;
    }
}
