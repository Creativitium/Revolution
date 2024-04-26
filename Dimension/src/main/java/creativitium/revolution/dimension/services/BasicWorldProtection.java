package creativitium.revolution.dimension.services;

import creativitium.revolution.dimension.Dimension;
import creativitium.revolution.dimension.data.CustomWorld;
import creativitium.revolution.foundation.templates.RService;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BasicWorldProtection extends RService
{
    private final Dimension plugin;

    public BasicWorldProtection()
    {
        super(Dimension.getInstance());
        this.plugin = Dimension.getInstance();
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getDropItemsOnDeath()))
            {
                event.setKeepInventory(true);
                event.getDrops().clear();
            }

            if (!player.hasPermission(world.getPermissions().getDie()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getTo().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getTo().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getAccess()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerJoinEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getAccess()))
            {
                Bukkit.getWorlds().stream().filter(w -> !plugin.getWorldManager().isCustomWorld(w) ||
                        player.hasPermission(plugin.getWorldManager().getCustomWorld(w.getName()).getPermissions().getAccess())).findFirst().ifPresentOrElse(w -> {
                    event.getPlayer().teleportAsync(w.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }, () -> event.getPlayer().kick(base.getMessageService().getMessage("dimension.no_worlds.accessible")));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getBlockBreak()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(PlayerInteractEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getGeneralInteract()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getBlockPlace()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEntityEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getEntityInteract()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityAttack(PrePlayerAttackEntityEvent event)
    {
        if (plugin.getWorldManager().isCustomWorld(event.getPlayer().getWorld()))
        {
            final Player player = event.getPlayer();
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(event.getPlayer().getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getEntityAttack()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityAttack(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player player && plugin.getWorldManager().isCustomWorld(player.getWorld()))
        {
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(player.getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getEntityAttack()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetLivingEntityEvent event)
    {
        if (event.getTarget() instanceof Player player && plugin.getWorldManager().isCustomWorld(player.getWorld()))
        {
            final CustomWorld world = plugin.getWorldManager().getCustomWorld(player.getWorld().getName());

            if (!player.hasPermission(world.getPermissions().getEntityTarget()))
            {
                event.setCancelled(true);
            }
        }
    }
}
