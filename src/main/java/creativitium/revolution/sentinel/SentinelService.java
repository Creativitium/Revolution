package creativitium.revolution.sentinel;

import creativitium.revolution.templates.RService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SentinelService extends RService
{
    private final Map<UUID, Ban> bans = new HashMap<>();

    @Override
    public void onStart()
    {
        bans.clear();
    }

    @Override
    public void onStop()
    {

    }

    public void addBan(UUID uuid, Ban ban)
    {
        bans.put(uuid, ban);

        if (Bukkit.getPlayer(uuid) != null)
        {
            Bukkit.getPlayer(uuid).kick(ban.getBanMessage());
        }
    }

    public void removeBan(UUID uuid, Ban ban)
    {
        bans.remove(uuid, ban);
    }

    public Optional<Map.Entry<UUID, Ban>> getBan(Player player, PlayerLoginEvent event)
    {
        return bans.entrySet().stream().filter(entry ->
        {
            Ban banEntry = entry.getValue();
            return (banEntry.getIps().contains(event.getAddress().getHostAddress().trim())
                    || banEntry.getUsername().equalsIgnoreCase(player.getName())
                    || banEntry.getUuid().equals(player.getUniqueId())) && !banEntry.hasExpired();
        }).findAny();
    }

    public Optional<Map.Entry<UUID, Ban>> getBan(Player player)
    {
        return bans.entrySet().stream().filter(entry ->
        {
            Ban banEntry = entry.getValue();
            return (banEntry.getUsername().equalsIgnoreCase(player.getName())
                    || banEntry.getUuid().equals(player.getUniqueId())
                    || banEntry.getIps().contains(player.getAddress().getAddress().getHostAddress().trim())) && !banEntry.hasExpired();
        }).findAny();
    }

    public Optional<Map.Entry<UUID, Ban>> getBan(OfflinePlayer player)
    {
        return bans.entrySet().stream().filter(entry ->
        {
            Ban banEntry = entry.getValue();
            return (banEntry.getUsername().equalsIgnoreCase(player.getName())
                    || banEntry.getUuid().equals(player.getUniqueId())) && !banEntry.hasExpired();
        }).findAny();
    }

    public void kickPlayer(final Player player, CommandSender sender, String reason)
    {
        player.kick(getSentinelKickMessage(plugin.msg.getMessage("revolution.components.sentinel.kicked.header"),
                plugin.msg.getMessage("revolution.components.sentinel.kicked.template",
                        Placeholder.unparsed("by", sender.getName()),
                        Placeholder.component("reason", reason != null ?
                                plugin.msg.getMessage("revolution.components.sentinel.kicked.reason", Placeholder.unparsed("reason", reason)) : Component.empty()))));
    }

    public Component getSentinelKickMessage(Component header, Component message)
    {
        return plugin.msg.getMessage("revolution.components.sentinel.format", Placeholder.component("header", header),
                Placeholder.component("message", message));
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        getBan(event.getPlayer(), event).ifPresent(entry -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                entry.getValue().getBanMessage()));
    }
}
