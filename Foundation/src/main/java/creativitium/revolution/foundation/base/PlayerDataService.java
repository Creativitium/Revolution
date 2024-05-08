package creativitium.revolution.foundation.base;

import creativitium.revolution.foundation.templates.RPlayerService;
import creativitium.revolution.foundation.templates.RService;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>PlayerDataService</h1>
 * <p>Manages RPlayerService implementations from other plugins.</p>
 */
public class PlayerDataService extends RService
{
    private final Map<Key, RPlayerService<?>> playerDataMap = new HashMap<>();

    @Override
    public void onStart()
    {
        playerDataMap.clear();
    }

    @Override
    public void onStop()
    {
        // Saves everything
        playerDataMap.forEach((plugin, service) -> service.onStop());
    }

    /**
     * Adds an RPlayerService to the player data map.
     * @param key       Key
     * @param service   RPlayerService
     */
    public void addExternalPlayerService(Key key, RPlayerService<?> service)
    {
        addExternalPlayerService(key, service, true);
    }

    /**
     * Adds an RPlayerService to the player data map.
     * @param key       Key
     * @param service   RPlayerService
     * @param start     boolean
     */
    public void addExternalPlayerService(Key key, RPlayerService<?> service, boolean start)
    {
        if (playerDataMap.containsKey(key))
        {
            throw new IllegalArgumentException("That external player data service has already been registered");
        }

        playerDataMap.put(key, service);

        if (start)
        {
            service.onStart();
        }
    }

    /**
     * Gets an RPlayerService from a Key.
     * @param key   Key
     * @return      RPlayerService
     */
    public RPlayerService<?> getService(Key key)
    {
        return playerDataMap.get(key);
    }
}
