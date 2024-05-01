package creativitium.revolution.administration.data;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.utilities.Util;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.time.Instant;
import java.util.*;

@Data
@Builder
public class Ban
{
    private String username;
    private UUID uuid;
    @Builder.Default
    private List<String> ips = new ArrayList<>();
    private long issued;
    private String reason;
    private String by;
    private UUID byUuid;
    private long expires;

    public boolean isExpired()
    {
        return Instant.now().getEpochSecond() >= expires;
    }

    public boolean hasUsername()
    {
        return username != null;
    }

    public boolean hasUuid()
    {
        return uuid != null;
    }

    public Component craftBanMessage()
    {
        return Foundation.getInstance().getMessageService().getMessage("administration.ban.message",
                Placeholder.component("reason", reason != null ?
                        Foundation.getInstance().getMessageService().getMessage("administration.ban.reason",
                                Placeholder.unparsed("reason", reason)) : Component.empty()),
                Placeholder.component("expires", expires != Long.MAX_VALUE ?
                        Foundation.getInstance().getMessageService().getMessage("administration.ban.expires",
                                Placeholder.unparsed("expires", Util.DATE_FORMAT.format(new Date(expires * 1000)))) : Component.empty()),
                Placeholder.unparsed("by", by));
    }

    public Map<String, Object> toMap()
    {
        final HashMap<String, Object> map = new HashMap<>();

        map.put("username", username);
        map.put("uuid", uuid.toString());
        map.put("ips", ips);
        map.put("issued", issued);
        if (reason != null) map.put("reason", reason);
        map.put("by", by);
        if (byUuid != null) map.put("by_uuid", byUuid.toString());
        map.put("expires", expires);

        return map;
    }
}
