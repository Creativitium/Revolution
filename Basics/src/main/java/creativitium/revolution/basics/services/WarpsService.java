package creativitium.revolution.basics.services;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.Warp;
import creativitium.revolution.foundation.templates.RService;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpsService extends RService
{
    private Map<String, Warp> warps = new HashMap<>();
    private final File warpsFile = new File(getPlugin().getDataFolder(), "warps.yml");

    public WarpsService()
    {
        super(Basics.getInstance());
    }

    @Override
    public void onStart()
    {
        load();
    }

    @Override
    public void onStop()
    {
        save();
    }

    public void load()
    {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(warpsFile);
        configuration.getKeys(false).forEach(key -> warps.put(key, configuration.getSerializable(key, Warp.class)));
    }

    public void save()
    {
        final YamlConfiguration configuration = new YamlConfiguration();
        warps.forEach(configuration::set);
        try
        {
            configuration.save(warpsFile);
        }
        catch (Exception ex)
        {
            Basics.getInstance().getSLF4JLogger().error("Failed to save warps file!", ex);
        }
    }

    public int getWarpCount()
    {
        return warps.size();
    }

    public List<String> getWarpNames()
    {
        return warps.keySet().stream().sorted(String::compareTo).toList();
    }

    public List<String> getWarpNamesBy(UUID uuid)
    {
        return warps.entrySet().stream().filter(entry -> entry.getValue().getBy().equals(uuid)).map(Map.Entry::getKey).sorted(String::compareTo).toList();
    }

    public Optional<Warp> getWarp(String name)
    {
        return Optional.ofNullable(warps.get(name));
    }

    public boolean warpExists(String name)
    {
        return warps.containsKey(name);
    }

    public boolean addWarp(String name, Warp warp, Player requester)
    {
        if (warpExists(name))
        {
            if (warps.get(name).getBy() != requester.getUniqueId() && !requester.hasPermission("basics.command.warp.others"))
            {
                return false;
            }

            warps.remove(name);
        }

        warps.put(name, warp);
        return true;
    }

    public void removeWarp(String name)
    {
        warps.remove(name);
    }
}
