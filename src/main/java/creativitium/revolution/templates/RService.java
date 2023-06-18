package creativitium.revolution.templates;

import creativitium.revolution.Revolution;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class RService implements Listener
{
    protected final Revolution plugin = Revolution.getInstance();

    protected RService()
    {
        Bukkit.getPluginManager().registerEvents(this, Revolution.getInstance());
    }

    public abstract void onStart();

    public abstract void onStop();
}
