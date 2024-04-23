package creativitium.revolution.foundation.utilities;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.templates.RPlayerService;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Shortcuts
{
    public static RPlayerService<?> getExternalPlayerService(Plugin plugin)
    {
        return Foundation.getInstance().getPlayerDataService().getExternalPlayerService(plugin);
    }

    public static void broadcast(String message, TagResolver... placeholders)
    {
        Bukkit.broadcast(Foundation.getInstance().getMessageService().getMessage(message, placeholders));
    }
}
