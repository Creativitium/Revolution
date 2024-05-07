package creativitium.revolution.foundation.utilities;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.templates.RPlayerService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Shortcuts
{
    public static RPlayerService<?> getService(Key key)
    {
        return Foundation.getInstance().getPlayerDataService().getService(key);
    }

    @Deprecated
    public static RPlayerService<?> getExternalPlayerService(Plugin plugin)
    {
        return Foundation.getInstance().getPlayerDataService().getExternalPlayerService(plugin);
    }
}
