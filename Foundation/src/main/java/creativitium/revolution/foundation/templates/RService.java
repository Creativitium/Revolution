package creativitium.revolution.foundation.templates;

import creativitium.revolution.foundation.Foundation;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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

    /**
     * Constructor for an instance of RService that uses Foundation as the main class.
     */
    protected RService()
    {
        this(Foundation.getInstance());
    }

    public void onStart()
    {
        // Do nothing
    }

    public void onStop()
    {
        // Do nothing
    }

    protected final Component getMsg(String message, TagResolver... placeholders)
    {
        return base.getMessageService().getMessage(message, placeholders);
    }
}
