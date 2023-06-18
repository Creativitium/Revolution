package creativitium.revolution.foundation.templates;

import creativitium.revolution.foundation.Foundation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class RService implements Listener
{
    protected final Foundation base = Foundation.getInstance();

    @Getter
    private final Plugin plugin;

    public RService(final Plugin plugin)
    {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public RService()
    {
        this(Foundation.getInstance());
    }

    public abstract void onStart();

    public abstract void onStop();
}
