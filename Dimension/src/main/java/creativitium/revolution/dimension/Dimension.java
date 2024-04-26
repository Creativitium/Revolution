package creativitium.revolution.dimension;

import creativitium.revolution.dimension.services.WorldManager;
import creativitium.revolution.foundation.RServiceGroup;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Dimension extends JavaPlugin
{
    @Getter
    private static Dimension instance;

    @Getter
    private RServiceGroup group = new RServiceGroup();
    @Getter
    private WorldManager worldManager;

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        worldManager = group.addService(NamespacedKey.fromString("dim:world_mgr"), new WorldManager());
        group.startServices();
    }

    @Override
    public void onDisable()
    {
        group.stopServices();
    }
}
