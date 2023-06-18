package creativitium.revolution.sentinel;

import creativitium.revolution.Revolution;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Ban
{
    private static final Revolution plugin = Revolution.getInstance();
    //--
    private UUID uuid;
    private String username;
    private List<String> ips;
    private String reason = null;
    private String by;
    private long expires;

    public boolean hasExpired()
    {
        return Instant.now().getEpochSecond() >= expires;
    }

    public Component getBanMessage()
    {
        return plugin.sen.getSentinelKickMessage(plugin.msg.getMessage("revolution.components.sentinel.banned.header"),
                plugin.msg.getMessage("revolution.components.sentinel.banned.template",
                        Placeholder.unparsed("expiration", String.valueOf(expires)),
                        Placeholder.unparsed("by", by),
                        Placeholder.component("reason", reason != null ?
                                plugin.msg.getMessage("revolution.components.sentinel.banned.reason",
                                        Placeholder.unparsed("reason", reason)) : Component.empty())));
    }
}
