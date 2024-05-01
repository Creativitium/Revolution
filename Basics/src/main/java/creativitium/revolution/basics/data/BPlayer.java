package creativitium.revolution.basics.data;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.utilities.MM;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class BPlayer implements ConfigurationSerializable
{
    private String name = null;
    private Component nickname = null;
    private Component tag = null;
    private boolean berserkEnabled = false;
    private boolean godEnabled = false;
    private Map<String, Location> homes = new HashMap<>();
    private long lastOnline = 0L;
    private Location loginLocation = null;
    private Location lastLocation = null;
    private String lastIP = null;

    public BPlayer()
    {
    }

    public BPlayer(String name)
    {
        this.name = name;
    }

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("nickname", nickname != null ? MM.getNonExploitable().serialize(nickname) : null);
        map.put("tag", tag != null ? MM.getLessExploitable().serialize(tag) : null);
        map.put("berserkEnabled", berserkEnabled);
        map.put("godEnabled", godEnabled);
        map.put("homes", homes);
        map.put("loginLocation", loginLocation);
        // I really wish I didn't have to do this, but apparently I can't serialize Longs... for some retarded reason
        map.put("lastOnline", String.valueOf(lastOnline));

        return map;
    }

    public static BPlayer deserialize(Map<String, Object> map)
    {
        final BPlayer data = new BPlayer();
        data.name = (String) map.getOrDefault("name", null);
        data.nickname = map.get("nickname") != null ? MM.getNonExploitable().deserialize((String) map.get("nickname")) : null;
        data.tag = map.get("tag") != null ? MM.getLessExploitable().deserialize((String) map.get("tag")) : null;
        data.berserkEnabled = (boolean) map.getOrDefault("berserkEnabled", false);
        data.godEnabled = (boolean) map.getOrDefault("godEnabled", false);
        try
        {
            data.homes = (Map<String, Location>) map.getOrDefault("homes", new HashMap<>());
        }
        catch (Exception ex)
        {
            Basics.getInstance().getSLF4JLogger().warn("Unable to load homes for player {}", data.name);
        }
        try
        {
            data.loginLocation = (Location) map.getOrDefault("loginLocation", null);
        }
        catch (Exception ex)
        {
            Basics.getInstance().getSLF4JLogger().warn("Unable to load last login location for player {}", data.name);
        }
        // My hope is that this is so awful that Bukkit's API developers learn that Long is a thing that exists
        data.lastOnline = Long.parseLong((String) map.getOrDefault("lastOnline", "0"));

        return data;
    }
}
