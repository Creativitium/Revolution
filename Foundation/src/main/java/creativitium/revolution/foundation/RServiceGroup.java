package creativitium.revolution.foundation;

import creativitium.revolution.foundation.templates.RService;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>RServiceGroup</h1>
 * <p>Class that defines a group of RService instances.</p>
 */
public class RServiceGroup
{
    private final Map<NamespacedKey, RService> services = new HashMap<>();

    /**
     * Add an RService instance to this group.
     * @param tag       NamespacedKey
     * @param service   RService
     * @return          RService
     * @param <T>       RService
     */
    public <T extends RService> T addService(NamespacedKey tag, T service)
    {
        if (services.containsKey(tag))
        {
            throw new IllegalArgumentException("That service has already been registered");
        }

        services.put(tag, service);
        return service;
    }

    /**
     * Get an RService instance that is part of this group.
     * @param tag   NamespacedKey
     * @return      RService
     * @param <T>   RService
     */
    public <T extends RService> T getService(NamespacedKey tag)
    {
        return (T) services.get(tag);
    }

    /**
     * Start all the services in this group.
     */
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
                Foundation.getSlf4jLogger().warn("Failed to start service {}", service.getClass().getName(), ex);
            }
        });
    }

    /**
     * Stop all the services in this group.
     */
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
                Foundation.getSlf4jLogger().warn("Failed to stop service {}", service.getClass().getName(), ex);
            }
        });
    }
}
