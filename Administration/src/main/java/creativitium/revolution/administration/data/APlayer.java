package creativitium.revolution.administration.data;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class APlayer implements ConfigurationSerializable
{
    private boolean adminChatEnabled = false;
    private boolean commandSpyEnabled = false;

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("adminChatEnabled", adminChatEnabled);
        map.put("commandSpyEnabled", commandSpyEnabled);

        return map;
    }

    public static APlayer deserialize(Map<String, Object> map)
    {
        final APlayer data = new APlayer();
        data.adminChatEnabled = (boolean) map.getOrDefault("adminChatEnabled", false);
        data.commandSpyEnabled = (boolean) map.getOrDefault("commandSpyEnabled", false);
        return data;
    }
}
