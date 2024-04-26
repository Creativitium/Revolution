package creativitium.revolution.basics.services;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.MM;
import creativitium.revolution.foundation.utilities.Shortcuts;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BasicsService extends RService
{
    private static final List<EntityPotionEffectEvent.Cause> GOD_IMMUNE_EFFECTS = List.of(
            EntityPotionEffectEvent.Cause.POTION_SPLASH,
            EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD,
            EntityPotionEffectEvent.Cause.ARROW,
            EntityPotionEffectEvent.Cause.ATTACK,
            EntityPotionEffectEvent.Cause.FOOD,
            EntityPotionEffectEvent.Cause.WITHER_ROSE,
            EntityPotionEffectEvent.Cause.WARDEN,
            EntityPotionEffectEvent.Cause.PATROL_CAPTAIN,
            EntityPotionEffectEvent.Cause.BEACON,
            EntityPotionEffectEvent.Cause.DOLPHIN
    );

    public BasicsService()
    {
        super(Basics.getInstance());
    }

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
        final Player player = event.getPlayer();
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId());

        // Update last known player name
        if (!data.getName().equalsIgnoreCase(player.getName()))
        {
            data.setName(player.getName());
        }

        if (data.getNickname() != null)
        {
            player.displayName(data.getNickname());
        }

        event.joinMessage(base.getMessageService().getMessage("basics.components.join_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.unparsed("username", player.getName())));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        event.quitMessage(base.getMessageService().getMessage("basics.components.leave_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.unparsed("username", player.getName())));
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event)
    {
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(event.getPlayer().getUniqueId());

        event.renderer((source, sourceDisplayName, message, viewer) -> base.getMessageService().getMessage("basics.components.chat",
                Placeholder.component("tag", data.getTag() != null ? data.getTag().append(Component.space()) : Component.empty()),
                Placeholder.component("display", event.getPlayer().displayName()),
                Placeholder.component("nickname", data.getNickname() != null ? data.getNickname() : Component.text(event.getPlayer().getName())),
                Placeholder.unparsed("name", event.getPlayer().getName()),
                Placeholder.component("message", message)));
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player player && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }

        if (event.getDamager() instanceof Player player
                && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isBerserkEnabled()
                && player.getItemInHand().getType() == Material.AIR
                && !event.isCancelled())
        {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, Double.MAX_VALUE);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event)
    {
        if (event.getEntity() instanceof Player player
                && (GOD_IMMUNE_EFFECTS.contains(event.getCause()))
                && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }
}
