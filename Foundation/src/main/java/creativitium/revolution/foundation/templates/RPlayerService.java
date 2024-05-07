package creativitium.revolution.foundation.templates;

import creativitium.revolution.foundation.Foundation;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>RPlayerService</h1>
 * <p>Handles mission-critical information like player data for other plugins</p>
 */
@Getter
public abstract class RPlayerService<T extends ConfigurationSerializable> extends RService
{
    private YamlConfiguration configuration = new YamlConfiguration();
    private final Map<UUID, T> data = new HashMap<>();
    private final String filename;

    public RPlayerService(Plugin plugin, String filename)
    {
        super(plugin);
        this.filename = filename;
    }

    public RPlayerService(Plugin plugin)
    {
        this(plugin, "playerdata.yml");
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
        final File dataFile = new File(getPlugin().getDataFolder(), filename);

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
            configuration.save(new File(getPlugin().getDataFolder(), filename));
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

    public boolean hasPlayerData(UUID uuid)
    {
        return data.containsKey(uuid);
    }

    public abstract T createPlayerData(UUID uuid);

    public abstract Optional<T> getPlayerData(String name);

    public Collection<T> getAllPlayerData()
    {
        return data.values();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        // Saves player data asynchronously
        CompletableFuture.runAsync(this::save);
    }
}
