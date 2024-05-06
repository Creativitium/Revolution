package creativitium.revolution.regulation.services;

import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.regulation.Regulation;
import creativitium.revolution.regulation.Setting;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.block.data.type.Piston;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorldRegulator extends RService
{
    private final Map<World, YamlConfiguration> worldRegulations = new HashMap<>();
    private final File folder;

    public WorldRegulator(File folder)
    {
        super(Regulation.getInstance());
        this.folder = folder;

    }

    @Override
    public void onStart()
    {
        loadAll();
    }

    @Override
    public void onStop()
    {
        /*worldRegulations.forEach((world, config) ->
        {
            final File worldFile = new File(folder, world.getName() + ".yml");
            try
            {
                config.save(worldFile);
            }
            catch (IOException ex)
            {
                getPlugin().getSLF4JLogger().warn("Failed to save world configuration file", ex);
            }
        });*/
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event)
    {
        if (!worldRegulations.containsKey(event.getWorld()))
        {
            worldRegulations.put(event.getWorld(), loadWorldRegulations(event.getWorld()));
        }
    }

    /*--== Block Filtering ==--*/
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (Setting.BLOCK_FILTERING.getBoolean(world, false)
                && Setting.ITEM_BLACKLIST.getStringList(world).contains(event.getBlock().getType().name()))
        {
            event.getPlayer().sendMessage(base.getMessageService().getMessage("regulation.component.block_filtering.filtered_" + (event.getBlock().getType().isBlock() ? "block" : "item")));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final World world = event.getPlayer().getWorld();
        final Player player = event.getPlayer();

        if (Setting.BLOCK_FILTERING.getBoolean(world, false)
                && event.getItem() != null
                && Setting.ITEM_BLACKLIST.getStringList(world).contains(event.getItem().getType().name()))
        {
            player.getInventory().remove(event.getItem().getType());
            player.sendMessage(base.getMessageService().getMessage("regulation.component.block_filtering.filtered_" + (event.getItem().getType().isBlock() ? "block" : "item")));
            event.setCancelled(true);
        }
    }

    /*--== Entity Filtering ==--*/
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (Setting.ENTITY_FILTERING.getBoolean(world) && event.getEntityType() != EntityType.PLAYER
                && Setting.ENTITY_BLACKLIST.getStringList(world).contains(event.getEntity().getType().name()))
        {
            event.setCancelled(true);
        }
    }

    /*--== Explosions ==--*/
    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event)
    {
        final World world = event.getEntity().getWorld();

        if (!Setting.EXPLOSIONS.getBoolean(world, true))
        {
            event.setCancelled(true);
        }
    }

    /*--== Minecarts ==--*/
    @EventHandler
    public void onMinecartSpawn(EntitySpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (!Setting.MINECARTS.getBoolean(world, true) && event.getEntityType().name().contains("MINECART"))
        {
            event.setCancelled(true);
        }
    }

    /*--== Decaying Leaves ==--*/
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.DECAYING_LEAVES.getBoolean(world))
        {
            event.setCancelled(true);
        }
    }

    /*--== Redstone ==--*/
    @EventHandler
    public void onRedstonePulse(BlockRedstoneEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.REDSTONE.getBoolean(world))
        {
            event.setNewCurrent(0);
        }
    }

    @EventHandler
    public void onRedstonePulse(BlockPhysicsEvent event)
    {
        final World world = event.getBlock().getWorld();
        final BlockData data = event.getBlock().getBlockData();

        if (!Setting.REDSTONE.getBoolean(world) && (data instanceof Powerable || data instanceof AnaloguePowerable
                || data instanceof Dispenser || data instanceof Hopper || data instanceof Piston))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRedstonePulse(BlockPistonExtendEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.REDSTONE.getBoolean(world))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRedstonePulse(BlockPistonRetractEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.REDSTONE.getBoolean(world))
        {
            event.setCancelled(true);
        }
    }

    /*--== Block Physics ==--*/
    @EventHandler
    public void onBlockPhysics(EntityChangeBlockEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.PHYSICS.getBoolean(world) && event.getEntityType() == EntityType.FALLING_BLOCK)
        {
            event.setCancelled(true);
        }
    }

    /*--== Entity NBT ==--*/
    @EventHandler
    public void onEntitySpawnFromDispenser(BlockDispenseEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.SPAWN_EGG_NBT.getBoolean(world) && event.getItem().getType().name().endsWith("_SPAWN_EGG"))
        {
            event.setItem(new ItemStack(event.getItem().getType()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawnByPlayerInteraction(PlayerInteractEvent event)
    {
        final World world = event.getPlayer().getWorld();

        /* This solution isn't great, but I couldn't find a way to spawn entities of the type of the spawn egg itself,
        *   and parsing the name of the spawn egg to get the entity type isn't an option as some entities (like Snow
        *   Golems and Mooshrooms) don't have an equivalent in EntityType. So, the next best thing is to cancel the
        *   event if the item in their hand is a spawn egg and has a custom entity type in its NBT. */
        if (!Setting.SPAWN_EGG_NBT.getBoolean(world) && event.getAction().isRightClick()
                && event.getItem() != null && event.getItem().getType().name().endsWith("_SPAWN_EGG")
                && ((SpawnEggMeta) event.getItem().getItemMeta()).getCustomSpawnedType() != null)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (!Setting.SPAWN_EGG_NBT.getBoolean(world) && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DISPENSE_EGG)
        {
            event.setCancelled(true);
            world.spawnEntity(event.getLocation(), event.getEntityType(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    /*--== Spawners ==--*/
    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (!Setting.SPAWNERS.getBoolean(world))
        {
            event.setCancelled(true);
        }
    }

    /*--== Mob Limiting ==--*/
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        final World world = event.getLocation().getWorld();
        if (!Setting.MOB_LIMITING.getBoolean(event.getLocation().getWorld())) return;

        if (!List.of(CreatureSpawnEvent.SpawnReason.CUSTOM,
                CreatureSpawnEvent.SpawnReason.COMMAND,
                CreatureSpawnEvent.SpawnReason.SPAWNER_EGG).contains(event.getSpawnReason())
                || world.getEntitiesByClass(Mob.class).size() > Setting.MOB_LIMIT.getInt(world, 50))
        {
            event.setCancelled(true);
        }
    }

    public void loadAll()
    {
        worldRegulations.clear();
        Bukkit.getWorlds().forEach(world -> worldRegulations.put(world, loadWorldRegulations(world)));
    }

    public YamlConfiguration getRegulations(World world)
    {
        if (!worldRegulations.containsKey(world))
        {
            worldRegulations.put(world, loadWorldRegulations(world));
        }

        return worldRegulations.get(world);
    }

    private YamlConfiguration loadWorldRegulations(@NotNull World world)
    {
        final File worldFile = new File(folder, world.getName() + ".yml");
        YamlConfiguration config;

        if (worldFile.exists())
        {
            config = YamlConfiguration.loadConfiguration(worldFile);
        }
        else
        {
            try
            {
                Files.copy(Objects.requireNonNull(getPlugin().getResource("defaults.yml")), worldFile.toPath());
                config = YamlConfiguration.loadConfiguration(worldFile);
            }
            catch (IOException ex)
            {
                getPlugin().getSLF4JLogger().error("Failed to copy defaults", ex);
                config = new YamlConfiguration();
            }
        }

        return config;
    }
}
