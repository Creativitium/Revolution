package creativitium.revolution.administration.commands;

import com.google.common.net.InetAddresses;
import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.services.BanService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "unbanip",
        description = "Unban an IP address.",
        usage = "/unbanip <ip>",
        aliases = {"unban-ip"},
        permission = "administration.command.unbanip",
        source = SourceType.BOTH)
public class UnbanIPCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        final String ipAddress = args[0].trim();

        if (!InetAddresses.isInetAddress(ipAddress))
        {
            msg(sender, "administration.command.banip.invalid_ip_address");
            return true;
        }

        final BanService bs = Administration.getInstance().getBanService();
        bs.getEntryByIP(ipAddress).ifPresentOrElse(entry ->
        {
            action(sender, "administration.action.unbanip", Placeholder.unparsed("ip", ipAddress.trim()));
            bs.removeEntry(entry.getUuid());
        }, () -> msg(sender, "administration.command.unbanip.not_banned"));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}