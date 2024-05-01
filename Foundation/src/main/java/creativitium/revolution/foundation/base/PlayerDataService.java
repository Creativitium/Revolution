package creativitium.revolution.foundation.base;

import creativitium.revolution.foundation.templates.RPlayerService;
import creativitium.revolution.foundation.templates.RService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>PlayerDataService</h1>
 * <p>Manages RPlayerService implementations from other plugins.</p>
 */
public class PlayerDataService extends RService
{
    private final Map<Plugin, RPlayerService<?>> playerDataMap = new HashMap<>();

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

    public void addExternalPlayerService(Plugin plugin, RPlayerService<?> service)
    {
        addExternalPlayerService(plugin, service, true);
    }

    public void addExternalPlayerService(Plugin plugin, RPlayerService<?> service, boolean start)
    {
        if (playerDataMap.containsKey(plugin))
        {
            throw new IllegalArgumentException("That external player data service has already been registered");
        }

        playerDataMap.put(plugin, service);

        if (start)
        {
            service.onStart();
        }
    }

    public RPlayerService<?> getExternalPlayerService(Plugin plugin)
    {
        return playerDataMap.get(plugin);
    }
}
