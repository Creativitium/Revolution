package creativitium.revolution.basics.data;

import creativitium.revolution.foundation.utilities.MM;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class Warp implements ConfigurationSerializable
{
    public final UUID by;
    public Location position;

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("by", by.toString());
        map.put("position", position);

        return map;
    }

    public static Warp deserialize(Map<String, Object> map)
    {
        return Warp.builder().by(UUID.fromString((String) map.get("by"))).position((Location) map.get("position")).build();
    }
}
