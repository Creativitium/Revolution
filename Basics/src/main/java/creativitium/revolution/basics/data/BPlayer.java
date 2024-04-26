package creativitium.revolution.basics.data;

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
        data.homes = (Map<String, Location>) map.getOrDefault("homes", new HashMap<>());
        return data;
    }
}
