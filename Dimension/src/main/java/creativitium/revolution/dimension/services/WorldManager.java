package creativitium.revolution.dimension.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import creativitium.revolution.dimension.Dimension;
import creativitium.revolution.dimension.data.CustomWorld;
import creativitium.revolution.foundation.templates.RService;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class WorldManager extends RService
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private WorldConfig config = null;

    public WorldManager()
    {
        super(Dimension.getInstance());
    }

    @Override
    public void onStart()
    {
        getPlugin().getSLF4JLogger().info("World management service started");
        final File file = new File(getPlugin().getDataFolder(), "worlds.json");
        if (!file.exists())
        {
            try
            {
                // It doesn't auto-create data folders until you tell it to, for some reason?
                if (!getPlugin().getDataFolder().exists())
                {
                    getPlugin().getDataFolder().mkdirs();
                }

                // Build a configuration from scratch using defaults.
                this.config = WorldConfig.builder()
                        .settings(WorldConfig.Settings.builder().enabled(true).build())
                        .build();

                this.config.addWorlds(getDefaults());

                // Write it to disk
                FileWriter writer = new FileWriter(file);
                gson.toJson(this.config, writer);
                writer.close();
            }
            catch (Exception ex)
            {
                getPlugin().getSLF4JLogger().error("Failed to write world configuration file!", ex);
            }
        }
        else
        {
            try
            {
                getPlugin().getSLF4JLogger().info("Loading worlds configuration file...");
                WorldConfig worldConfig = gson.fromJson(new FileReader(file), WorldConfig.class);

                if (this.config == null)
                {
                    this.config = worldConfig;
                }
                else
                {
                    getPlugin().getSLF4JLogger().info("Combining what's in memory already...");
                    this.config.combine(worldConfig);
                }
            }
            catch (Exception ex)
            {
                getPlugin().getSLF4JLogger().error("Failed to load the world configuration. Worlds will not be generated!", ex);
            }
        }

        if (this.config != null && this.config.settings.enabled)
        {
            this.config.worlds.values().forEach(CustomWorld::load);
        }
    }

    public boolean isCustomWorld(World world)
    {
        return config.worlds.containsKey(world.getName());
    }

    public CustomWorld getCustomWorld(String name)
    {
        return config.worlds.get(name);
    }

    @Override
    public void onStop()
    {
        if (this.config != null && this.config.settings.enabled)
            this.config.worlds.values().forEach(world -> world.getWorld().save());
    }

    private List<CustomWorld> getDefaults()
    {
        return List.of(
                CustomWorld.builder().name("adminworld")
                        .command(CustomWorld.Command.builder()
                                .enabled(true)
                                .description("Teleport to the adminworld.")
                                .usage("/adminworld")
                                .aliases(new String[0])
                                .permission("dimension.command.adminworld").build())
                        .generation(CustomWorld.Generation.builder()
                                .environment(World.Environment.NORMAL)
                                .generateStructures(false)
                                .generator("CleanroomGenerator")
                                .seed(Instant.now().getEpochSecond()).build())
                        .flags(CustomWorld.Flags.builder()
                                .autosave(true)
                                .difficulty(Difficulty.PEACEFUL)
                                .hardcore(false)
                                .pvp(true)
                                .spawn(CustomWorld.Flags.SpawnFlags.builder()
                                        .allowMonsters(false)
                                        .allowAnimals(false)
                                        .keepInMemory(true).build()).build())
                        .permissions(CustomWorld.Permissions.builder()
                                .access("dimension.adminworld.access")
                                .blockBreak("dimension.adminworld.block_break")
                                .blockPlace("dimension.adminworld.block_place")
                                .die("dimension.adminworld.die")
                                .entityAttack("dimension.adminworld.entity_attack")
                                .entityInteract("dimension.adminworld.entity_interact")
                                .entityTarget("dimension.adminworld.entity_target")
                                .generalInteract("dimension.adminworld.general_interact")
                                .dropItemsOnDeath("dimension.adminworld.drop_items_on_death").build()).build(),
                CustomWorld.builder().name("flatlands")
                        .command(CustomWorld.Command.builder()
                                .enabled(true)
                                .description("Teleport to the flatlands.")
                                .usage("/flatlands")
                                .aliases(new String[0])
                                .permission("dimension.command.flatlands").build())
                        .generation(CustomWorld.Generation.builder()
                                .environment(World.Environment.NORMAL)
                                .generateStructures(false)
                                .generator("CleanroomGenerator")
                                .seed(Instant.now().getEpochSecond()).build())
                        .flags(CustomWorld.Flags.builder()
                                .autosave(true)
                                .difficulty(Difficulty.PEACEFUL)
                                .hardcore(false)
                                .pvp(true)
                                .spawn(CustomWorld.Flags.SpawnFlags.builder()
                                        .allowMonsters(false)
                                        .allowAnimals(false)
                                        .keepInMemory(true).build()).build())
                        .permissions(CustomWorld.Permissions.builder()
                                .access("dimension.flatlands.access")
                                .blockBreak("dimension.flatlands.block_break")
                                .blockPlace("dimension.flatlands.block_place")
                                .die("dimension.flatlands.die")
                                .entityAttack("dimension.flatlands.entity_attack")
                                .entityInteract("dimension.flatlands.entity_interact")
                                .entityTarget("dimension.flatlands.entity_target")
                                .generalInteract("dimension.flatlands.general_interact")
                                .dropItemsOnDeath("dimension.flatlands.drop_items_on_death").build()).build()
        );
    }

    @Builder
    public static class WorldConfig
    {
        private Settings settings;
        @Builder.Default
        private Map<String, CustomWorld> worlds = new HashMap<>();

        public void combine(WorldConfig config)
        {
            config.worlds.forEach((key, value) ->
            {
                if (!worlds.containsKey(key))
                    worlds.put(key, value);
            });
        }

        public void addWorlds(List<CustomWorld> input)
        {
            input.forEach(world -> worlds.put(world.getName(), world));
        }

        @Builder
        public static class Settings
        {
            private boolean enabled;
        }
    }
}
