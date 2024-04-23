package creativitium.revolution.players;

import com.google.common.base.Strings;
import creativitium.revolution.templates.RService;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * <h1>PlayerListenerService</h1>
 * <p>Handles cosmetic information about players like their join messages and commands</p>
 */
public class PlayerListenerService extends RService
{
    @Override
    public void onStart()
    {
        
    }

    @Override
    public void onStop()
    {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData playerData = plugin.pls.getPlayerData(uuid);

        event.joinMessage(plugin.msg.getMessage("revolution.components.join_message", Placeholder.component("name", playerData.getNickname() != null ? playerData.getNickname() : event.getPlayer().displayName())));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData playerData = plugin.pls.getPlayerData(uuid);

        event.quitMessage(plugin.msg.getMessage("revolution.components.leave_message", Placeholder.component("name", playerData.getNickname() != null ? playerData.getNickname() : event.getPlayer().displayName())));
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event)
    {
        final PlayerData data = plugin.pls.getPlayerData(event.getPlayer().getUniqueId());
        final Component nickname = data.getNickname() != null && !Strings.isNullOrEmpty(PlainTextComponentSerializer.plainText().serialize(data.getNickname())) ? data.getNickname() : event.getPlayer().displayName().colorIfAbsent(NamedTextColor.RED);

        event.renderer((source, sourceDisplayName, message, viewer) -> plugin.msg.getMessage("revolution.components.chat",
                Placeholder.component("tag", data.getTag() != null ? data.getTag() : Component.empty()),
                Placeholder.component("name", nickname),
                Placeholder.component("message", message)));
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        Player sender = event.getPlayer();
        Bukkit.getOnlinePlayers().stream().filter(player -> !player.getUniqueId().equals(sender.getUniqueId())
                && player.hasPermission("revolution.command.commandspy")
                && plugin.pls.getPlayerData(player.getUniqueId()).isCommandSpyEnabled()).forEach(player ->
                player.sendMessage(plugin.msg.getMessage("administration.components.commandspy",
                        Placeholder.component("player", Component.text(sender.getName())
                                .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click")))
                                .clickEvent(ClickEvent.copyToClipboard(sender.getName()))),
                        Placeholder.unparsed("command", event.getMessage()))));
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && plugin.pls.getPlayerData(player.getUniqueId()).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player player && plugin.pls.getPlayerData(player.getUniqueId()).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event)
    {
        if (event.getEntity() instanceof Player player
                && (event.getCause() == EntityPotionEffectEvent.Cause.POTION_SPLASH
                    || event.getCause() == EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD
                    || event.getCause() == EntityPotionEffectEvent.Cause.ARROW
                    || event.getCause() == EntityPotionEffectEvent.Cause.ATTACK
                    || event.getCause() == EntityPotionEffectEvent.Cause.FOOD
                    || event.getCause() == EntityPotionEffectEvent.Cause.WITHER_ROSE
                    || event.getCause() == EntityPotionEffectEvent.Cause.BEACON)
                && plugin.pls.getPlayerData(player.getUniqueId()).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }
}
