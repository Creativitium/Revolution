package creativitium.revolution.foundation.utilities;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.templates.RPlayerService;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

public class Shortcuts
{
    public static RPlayerService<?> getExternalPlayerService(Plugin plugin)
    {
        return Foundation.getInstance().getPlayerDataService().getExternalPlayerService(plugin);
    }
}
