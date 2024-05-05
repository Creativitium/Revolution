package creativitium.revolution.regulation.services;

import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.regulation.Regulation;
import creativitium.revolution.regulation.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.block.data.type.Piston;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.material.SpawnEgg;
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
        worldRegulations.clear();

        Bukkit.getWorlds().forEach(world -> worldRegulations.put(world, loadWorldRegulations(world)));
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

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event)
    {
        final World world = event.getEntity().getWorld();

        if (!Setting.EXPLOSIONS.getBoolean(world, true))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (!Setting.MINECARTS.getBoolean(world, true) && event.getEntityType().name().contains("MINECART"))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.DECAYING_LEAVES.getBoolean(world))
        {
            event.setCancelled(true);
        }
    }

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

    @EventHandler
    public void onBlockPhysics(EntityChangeBlockEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.PHYSICS.getBoolean(world) && event.getEntityType() == EntityType.FALLING_BLOCK)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(BlockDispenseEvent event)
    {
        final World world = event.getBlock().getWorld();

        if (!Setting.SPAWN_EGG_NBT.getBoolean(world) && event.getItem().getType().name().endsWith("_SPAWN_EGG"))
        {
            event.setItem(new ItemStack(event.getItem().getType()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(PlayerInteractEvent event)
    {
        final World world = event.getPlayer().getWorld();

        if (!Setting.SPAWN_EGG_NBT.getBoolean(world) && event.getAction().isRightClick()
                && event.getItem() != null && event.getItem().getType().name().endsWith("_SPAWN_EGG")
                && ((SpawnEggMeta) event.getItem().getItemMeta()).getCustomSpawnedType() != null)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (!Setting.SPAWN_EGG_NBT.getBoolean(world) && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DISPENSE_EGG)
        {
            event.setCancelled(true);
            world.spawnEntity(event.getLocation(), event.getEntityType(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event)
    {
        final World world = event.getLocation().getWorld();

        if (!Setting.SPAWNERS.getBoolean(world))
        {
            event.setCancelled(true);
        }
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
