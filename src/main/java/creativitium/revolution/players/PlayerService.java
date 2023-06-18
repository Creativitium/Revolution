package creativitium.revolution.players;

import creativitium.revolution.Revolution;
import creativitium.revolution.templates.RService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>PlayerService</h1>
 * <p>Handles mission-critical information like player data</p>
 */
public class PlayerService extends RService
{
    private static final File dataFile = new File(Revolution.getInstance().getDataFolder(), "playerdata.yml");
    //--
    private YamlConfiguration configuration = new YamlConfiguration();
    private Map<UUID, PlayerData> data = new HashMap<>();

    @Override
    public void onStart()
    {
        load();
    }

    @Override
    public void onStop()
    {
        save();
    }

    public void load()
    {
        if (dataFile.exists())
        {
            data.clear();
            configuration = YamlConfiguration.loadConfiguration(dataFile);
            configuration.getKeys(false).forEach(key -> data.put(UUID.fromString(key), configuration.getSerializable(key, PlayerData.class)));
        }
    }

    public void save()
    {
        data.forEach((uuid, dataSet) -> configuration.set(uuid.toString(), dataSet));
        try
        {
            configuration.save(dataFile);
        }
        catch (Exception ex)
        {
            Revolution.getSlf4jLogger().error("Failed to save player data!", ex);
        }
    }

    public PlayerData getPlayerData(UUID uuid)
    {
        if (!data.containsKey(uuid))
        {
            PlayerData playerData = new PlayerData();
            Optional<Player> player = Optional.ofNullable(Bukkit.getPlayer(uuid));
            player.ifPresent(p ->
            {
                playerData.setNickname(Component.text(p.getName(), NamedTextColor.RED));
                playerData.setName(p.getName());
            });
            data.put(uuid, playerData);
        }

        return data.get(uuid);
    }

    public Optional<PlayerData> getPlayerData(String name)
    {
        return data.values().stream().filter(data -> data.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Collection<PlayerData> getAllPlayerData()
    {
        return data.values();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData playerData = plugin.pls.getPlayerData(uuid);

        if (playerData.getName() == null || !playerData.getName().equalsIgnoreCase(event.getPlayer().getName()))
            playerData.setName(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        // Saves player data asynchronously
        CompletableFuture.runAsync(() -> plugin.pls.save());
    }
}
