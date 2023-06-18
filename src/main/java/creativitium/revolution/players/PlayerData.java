package creativitium.revolution.players;

import creativitium.revolution.utilities.MM;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerData implements ConfigurationSerializable
{
    private Component nickname = null;
    private String name = null;
    private Component tag = Component.empty();
    private Double money = 0d;
    private Map<String, Location> homes = new HashMap<>();
    private boolean commandSpyEnabled = false;
    private boolean godEnabled = false;

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("nickname", nickname != null ? MM.getLessExploitable().serialize(nickname) : null);
        map.put("money", money);
        map.put("homes", homes);
        map.put("tag", MM.getLessExploitable().serialize(tag));
        map.put("commandSpyEnabled", commandSpyEnabled);
        map.put("godEnabled", godEnabled);

        return map;
    }

    public static PlayerData deserialize(Map<String, Object> map)
    {
        final PlayerData data = new PlayerData();

        data.name = (String) map.getOrDefault("name", null);
        // FUCKING PICKY BASTARD
        String nickname = (String) map.getOrDefault("nickname", "");
        data.nickname = nickname != null ? MM.getNonExploitable().deserialize(nickname) : null;
        //--
        data.money = (Double) map.getOrDefault("money", 0d);
        data.homes = (Map<String, Location>) map.getOrDefault("homes", new HashMap<>());
        data.tag = MM.getLessExploitable().deserialize((String) map.getOrDefault("tag", ""));
        data.commandSpyEnabled = (boolean) map.getOrDefault("commandSpyEnabled", false);
        data.godEnabled = (boolean) map.getOrDefault("godEnabled", false);

        return data;
    }
}
