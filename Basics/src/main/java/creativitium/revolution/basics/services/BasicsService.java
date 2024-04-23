package creativitium.revolution.basics.services;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.Shortcuts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BasicsService extends RService
{
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
                && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }
}
