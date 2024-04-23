package creativitium.revolution.foundation.templates;

import creativitium.revolution.foundation.Foundation;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>RPlayerService</h1>
 * <p>Handles mission-critical information like player data for other plugins</p>
 */
public abstract class RPlayerService<T extends ConfigurationSerializable> extends RService
{
    @Getter
    private YamlConfiguration configuration = new YamlConfiguration();
    @Getter
    private Map<UUID, T> data = new HashMap<>();

    public RPlayerService(Plugin plugin)
    {
        super(plugin);
    }

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
        final File dataFile = new File(getPlugin().getDataFolder(), "playerdata.yml");

        if (dataFile.exists())
        {
            data.clear();
            configuration = YamlConfiguration.loadConfiguration(dataFile);
            loadDataFromConfiguration();
            //configuration.getKeys(false).forEach(key -> data.put(UUID.fromString(key), configuration.getSerializable(key, T)));
        }
    }

    public abstract void loadDataFromConfiguration();

    public void save()
    {
        data.forEach((uuid, dataSet) -> configuration.set(uuid.toString(), dataSet));
        try
        {
            configuration.save(new File(getPlugin().getDataFolder(), "playerdata.yml"));
        }
        catch (Exception ex)
        {
            Foundation.getSlf4jLogger().error("Failed to save player data!", ex);
        }
    }

    public T getPlayerData(UUID uuid)
    {
        if (!data.containsKey(uuid))
        {
            T playerData = createPlayerData(uuid);
            data.put(uuid, playerData);
        }

        return data.get(uuid);
    }

    public abstract T createPlayerData(UUID uuid);

    public abstract Optional<T> getPlayerData(String name);

    public Collection<T> getAllPlayerData()
    {
        return data.values();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        // Saves player data asynchronously
        CompletableFuture.runAsync(this::save);
    }
}
