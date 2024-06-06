package creativitium.revolution.basics.data;

import creativitium.revolution.foundation.utilities.Util;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
public class MailMessage implements ConfigurationSerializable
{
    private final long timestamp;
    private final UUID senderId;
    private final @NotNull String senderName;
    private final @NotNull String message;
    @Builder.Default
    @Setter
    private boolean read = false;

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> map = new HashMap<>();

        map.put("timestamp", timestamp);
        map.put("senderId", senderId != null ? senderId.toString() : null);
        map.put("senderName", senderName);
        map.put("message", message);
        map.put("read", read);

        return map;
    }

    public static MailMessage deserialize(Map<String, Object> map)
    {
        return builder()
                .timestamp(Integer.toUnsignedLong((Integer) map.getOrDefault("timestamp", 0)))
                .senderId(map.containsKey("senderId") && Util.isUUIDValid((String) map.get("senderId")) ?
                        UUID.fromString((String) map.get("senderId")) : null)
                .senderName((String) map.getOrDefault("senderName", "Unknown"))
                .message((String) map.getOrDefault("message", ""))
                .read((boolean) map.getOrDefault("read", false))
                .build();
    }
}
