package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import creativitium.revolution.foundation.utilities.Util;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CommandParameters(name = "seen",
        description = "Returns the last known information about a player.",
        usage = "/seen <player>",
        permission = "basics.command.seen",
        source = SourceType.BOTH)
public class SeenCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final OfflinePlayer player = getOfflinePlayer(args[0]);

        if (!player.hasPlayedBefore())
        {
            msg(sender, "basics.command.seen.never_joined_before", Placeholder.unparsed("player", args[0]));
        }

        ((Optional<BPlayer>) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(args[0]))
                .ifPresent(data -> {
                    msg(sender, player.isOnline() ? "basics.command.seen.info.online" : "basics.command.seen.info.offline",
                            Placeholder.unparsed("username", data.getName()),
                            Placeholder.component("nickname", data.getNickname() != null ? data.getNickname() : Component.text(data.getName())),
                            Placeholder.unparsed("date", Util.DATE_FORMAT.format(new Date(data.getLastOnline() * 1000))));

                    if (sender.hasPermission("basics.command.seen.see_ips") && (player.isOnline() || data.getLastIP() != null))
                        msg(sender, "basics.command.seen.info.ip", Placeholder.parsed("ip", player.isOnline() ? Objects.requireNonNull(Objects.requireNonNull(player.getPlayer()).getAddress()).getAddress().getHostAddress() : data.getLastIP()));

                    if (sender.hasPermission("basics.command.seen.see_location") && (player.isOnline() || data.getLoginLocation() != null))
                    {
                        final Location location = player.isOnline() ? Objects.requireNonNull(player.getPlayer()).getLocation() : data.getLoginLocation();
                        msg(sender, "basics.command.seen.info.location", Placeholder.component("location",
                                getMessage("basics.command.seen.info.location.format",
                                        Placeholder.parsed("world", location.getWorld().getName()),
                                        Placeholder.parsed("x", String.valueOf((int) location.x())),
                                        Placeholder.parsed("y", String.valueOf((int) location.y())),
                                        Placeholder.parsed("z", String.valueOf((int) location.z())))));
                    }
                });

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
        return Basics.getInstance();
    }
}
