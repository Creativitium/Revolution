package creativitium.revolution.administration.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SPlayer implements ConfigurationSerializable
{
    private boolean muted;
    private boolean commandsBlocked;
    private boolean frozen;

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("muted", muted);
        map.put("commandsBlocked", commandsBlocked);
        map.put("frozen", frozen);

        return map;
    }

    public static SPlayer deserialize(Map<String, Object> map)
    {
        final SPlayer data = new SPlayer();

        data.muted = (boolean) map.getOrDefault("muted", false);
        data.commandsBlocked = (boolean) map.getOrDefault("commandsBlocked", false);
        data.frozen = (boolean) map.getOrDefault("frozen", false);

        return data;
    }
}
