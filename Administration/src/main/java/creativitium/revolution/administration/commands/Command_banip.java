package creativitium.revolution.administration.commands;

import com.google.common.net.InetAddresses;
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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@CommandParameters(name = "banip",
        description = "Ban an IP address from the server.",
        usage = "/banip <ip> [reason]",
        aliases = {"ban-ip"},
        permission = "administration.command.banip",
        source = SourceType.BOTH)
public class Command_banip extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        final String ipAddress = args[0].trim();
        final String reason = args.length > 1 ? StringUtils.join(ArrayUtils.remove(args, 0), " ") : null;

        if (!InetAddresses.isInetAddress(ipAddress))
        {
            msg(sender, "administration.command.banip.invalid_ip_address");
            return true;
        }

        final Ban ban = Ban.builder()
                .username(null)
                .uuid(UUID.nameUUIDFromBytes(ipAddress.getBytes()))
                .ips(Collections.singletonList(ipAddress))
                .issued(Instant.now().getEpochSecond())
                .reason(reason)
                .by(sender.getName())
                .byUuid(SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? playerSender.getUniqueId() : null)
                .expires(Util.getOffsetFromCurrentTime(24, Util.TimeUnit.HOURS)).build();

        Administration.getInstance().getBanService().addEntry(ban);

        action(sender, "administration.action.banip", Placeholder.unparsed("ip", ipAddress.trim()),
                Placeholder.component("reason", reason != null ? Foundation.getInstance().getMessageService().getMessage("administration.action.banip.reason", Placeholder.unparsed("reason", reason)) : Component.empty()));

        Bukkit.getOnlinePlayers().stream().filter(player -> player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(ipAddress)).forEach(player ->
                player.kick(ban.craftBanMessage()));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}