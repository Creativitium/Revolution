package creativitium.revolution.capitalism.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CPlayer implements ConfigurationSerializable
{
    private double balance = 0.00;

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("balance", balance);
        return map;
    }

    public static CPlayer deserialize(Map<String, Object> map)
    {
        final CPlayer data = new CPlayer();
        data.balance = Math.min((double) map.getOrDefault("balance", 0D), 1000000000000D);
        return data;
    }
}
