package creativitium.revolution.guardian;

import creativitium.revolution.templates.RService;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class GuardianService extends RService
{
    @Getter
    @Setter
    private boolean lockdown;

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    private Component getKickMessage(String header, String message, TagResolver... placeholders)
    {
        return plugin.msg.getMessage("revolution.components.guardian.format", Placeholder.component("header", plugin.msg.getMessage(header)),
                Placeholder.component("message", plugin.msg.getMessage(message, placeholders)));
    }

    public void shutdown()
    {
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(getKickMessage("revolution.components.guardian.shutdown.header",
                "revolution.components.guardian.shutdown.message")));
        Bukkit.shutdown();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        if (lockdown && !event.getPlayer().hasPermission("revolution.guardian.lockdown.bypass"))
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, getKickMessage(
                    "revolution.components.guardian.lockdown.header",
                    "revolution.components.guardian.lockdown.message"));
        }
    }
}
