package creativitium.revolution.administration.services;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.Ban;
import creativitium.revolution.foundation.templates.RService;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.net.InetAddress;
import java.time.Instant;
import java.util.*;

public class BanService extends RService
{
    @Getter
    private final Map<UUID, Ban> bans = new HashMap<>();
    @Getter
    private final File dataFile = new File(getPlugin().getDataFolder(), "bans.yml");
    @Getter
    private YamlConfiguration configuration = new YamlConfiguration();

    public BanService()
    {
        super(Administration.getInstance());
    }

    @Override
    public void onStart()
    {
        bans.clear();
        load();
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        if (event.getPlayer().hasPermission("administration.bypass.bans"))
        {
            return;
        }

        getEntryWherePossible(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getAddress()).ifPresent(entry ->
        {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, entry.craftBanMessage());
        });
    }

    public void addEntry(Ban ban)
    {
        removeEntry(ban.getUuid());
        bans.put(ban.getUuid(), ban);

        save();
    }

    public void removeEntry(UUID playerUUID)
    {
        bans.remove(playerUUID);

        save();
    }

    public Optional<Ban> getEntryByOfflinePlayer(OfflinePlayer player)
    {
        return bans.values().stream().filter(original -> original.getUuid().equals(player.getUniqueId())
                || original.getUsername().equalsIgnoreCase(player.getName())).findAny();
    }

    public Optional<Ban> getEntryByUUID(UUID player)
    {
        return Optional.ofNullable(bans.get(player));
    }

    public Optional<Ban> getEntryByUsername(String player)
    {
        return bans.values().stream().filter(original -> original.getUsername().equalsIgnoreCase(player)).findAny();
    }

    public Optional<Ban> getEntryByIP(InetAddress address)
    {
        return bans.values().stream().filter(entry -> entry.getIps().contains(address.getHostAddress())).findAny();
    }

    public Optional<Ban> getEntryWherePossible(UUID uuid, String username, InetAddress address)
    {
        if (bans.containsKey(uuid))
        {
            return Optional.of(bans.get(uuid));
        }

        return bans.values().stream().filter(entry -> entry.getUsername().equalsIgnoreCase(username)
                || entry.getIps().contains(address.getHostAddress())).findAny();
    }

    public void load()
    {
        if (dataFile.exists())
        {
            configuration = YamlConfiguration.loadConfiguration(dataFile);
            for (String ban : configuration.getKeys(false))
            {
                UUID uuid;

                // Enforce requirement that all bans use UUIDs to uniquely identify them;.
                try
                {
                    uuid = UUID.fromString(ban);
                }
                catch (Exception ex)
                {
                    continue;
                }

                // Is this a valid configuration section?
                if (!configuration.isConfigurationSection(ban))
                {
                    // Ignore invalid entry
                    continue;
                }

                ConfigurationSection section = configuration.getConfigurationSection(ban);

                // Has this entry expired?
                if (section.getLong("expires") <= Instant.now().getEpochSecond())
                {
                    // In that case, don't even bother trying to load it
                    continue;
                }

                bans.put(uuid, Ban.builder()
                        .username(section.getString("username", "Unknown"))
                        .uuid(UUID.fromString(Objects.requireNonNull(section.getString("uuid"))))
                        .ips(section.getStringList("ips"))
                        .issued(section.getLong("issued"))
                        .reason(section.getString("reason", null))
                        .by(section.getString("by", "Unknown"))
                        .byUuid(section.getString("by_uuid", null) != null ? UUID.fromString(Objects.requireNonNull(configuration.getString(ban + ".by_uuid"))) : null)
                        .expires(section.getLong("expires", 253402300799L)).build());
            }
        }
    }

    public void save()
    {
        // This is a pretty ugly way of removing everything from a configuration, but it works so ¯\_(ツ)_/¯
        for (String key : configuration.getKeys(false))
        {
            configuration.set(key, null);
        }

        for (Map.Entry<UUID, Ban> ban : bans.entrySet())
        {
            configuration.set(ban.getKey().toString(), ban.getValue().toMap());
        }

        try
        {
            configuration.save(dataFile);
        }
        catch (Exception ex)
        {
            Administration.getInstance().getSLF4JLogger().error("Failed to save configuration", ex);
        }
    }
}
