package creativitium.revolution;

import creativitium.revolution.templates.RService;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class RServiceGroup
{
    private Map<NamespacedKey, RService> services = new HashMap<>();

    public <T extends RService> T addService(NamespacedKey tag, T service)
    {
        if (services.containsKey(tag))
        {
            throw new IllegalArgumentException("That service has already been registered");
        }

        services.put(tag, service);
        return service;
    }

    public <T extends RService> T getService(NamespacedKey tag)
    {
        return (T) services.get(tag);
    }

    public void startServices()
    {
        services.values().forEach(service ->
        {
            try
            {
                service.onStart();
            }
            catch (Throwable ex)
            {
                Revolution.getSlf4jLogger().warn("Failed to start service " + service.getClass().getName());
            }
        });
    }

    public void stopServices()
    {
        services.values().forEach(service ->
        {
            try
            {
                service.onStop();
            }
            catch (Throwable ex)
            {
                Revolution.getSlf4jLogger().warn("Failed to stop service " + service.getClass().getName());
            }
        });
    }
}
