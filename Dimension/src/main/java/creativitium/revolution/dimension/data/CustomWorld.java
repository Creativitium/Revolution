package creativitium.revolution.dimension.data;

import creativitium.revolution.dimension.Dimension;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.bukkit.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class CustomWorld
{
    private String name;
    @Builder.Default
    private Command command = null;
    private Generation generation;
    private Flags flags;
    @Builder.Default
    private Permissions permissions = Permissions.builder().build();
    //--
    private World world = null;

    public final void load()
    {
        // Generate the world if it isn't present already
        if (world == null || Bukkit.getWorld(name) == null || !Bukkit.getWorlds().contains(world))
        {
            world = generate();
        }

        // Register the command if it's enabled and hasn't been registered already
        if (command != null && command.enabled && Bukkit.getCommandMap().getCommand(name.replace(" ", "_").toLowerCase()) == null)
            Dimension.getInstance().getCommandLoader().loadCommandsManually("dimension", new WorldCommand(this));
    }

    public final World generate()
    {
        final WorldCreator creator = new WorldCreator(name);

        // Environment
        if (generation != null)
        {
            creator.environment(generation.environment);
            creator.type(generation.type);
            creator.generateStructures(generation.generateStructures);
            if (generation.generator != null) creator.generator(generation.generator);
            if (generation.generatorSettings != null) creator.generatorSettings(generation.generatorSettings);
            if (generation.biomeProvider != null) creator.biomeProvider(generation.biomeProvider);
            creator.seed(generation.seed);
        }

        world = creator.createWorld();

        // The world should have generated, but if it hasn't then we've got some serious problems
        if (world == null)
        {
            throw new IllegalStateException("World did not generate properly.");
        }

        // Flags
        if (flags != null)
        {
            world.setAutoSave(flags.autosave);
            world.setDifficulty(flags.difficulty);
            world.setHardcore(flags.hardcore);
            world.setPVP(flags.pvp);

            if (flags.spawn != null)
            {
                world.setSpawnFlags(flags.spawn.allowMonsters, flags.spawn.allowAnimals);
                world.setKeepSpawnInMemory(flags.spawn.keepInMemory);
            }

            if (flags.distances != null)
            {
                if (flags.distances.sendViewDistance != -1) world.setSendViewDistance(flags.distances.sendViewDistance);
                if (flags.distances.simulationDistance != -1) world.setSimulationDistance(flags.distances.simulationDistance);
            }

            // Sets the gamerules for the world
            flags.gameRules.forEach((rule, value) ->
            {
                final GameRule gameRule = GameRule.getByName(rule);
                if (gameRule == null)
                {
                    Dimension.getInstance().getSLF4JLogger().warn("Skipping unknown gamerule {}", rule);
                    return;
                }
                else if (!gameRule.getType().isInstance(value))
                {
                    Dimension.getInstance().getSLF4JLogger().warn("Skipping gamerule of incorrect type {}", rule);
                    return;
                }

                world.setGameRule(gameRule, value);
            });
        }

        return world;
    }

    @Builder
    @Getter
    public static class Command
    {
        private boolean enabled;
        private String description;
        private String usage;
        @Builder.Default
        private String[] aliases = {""};
        private String permission;
    }

    @Builder
    @Getter
    public static class Flags
    {
        @Builder.Default
        private boolean autosave = true;
        @Builder.Default
        private Difficulty difficulty = Difficulty.EASY;
        private boolean hardcore;
        private boolean pvp;
        private SpawnFlags spawn;
        private Distances distances;
        @Builder.Default
        private Map<String, Object> gameRules = new HashMap<>();

        @Builder
        public static class SpawnFlags
        {
            private boolean allowAnimals;
            private boolean allowMonsters;
            @Builder.Default
            private boolean keepInMemory = true;
        }

        @Builder
        public static class Distances
        {
            @Builder.Default
            private int sendViewDistance = -1;
            @Builder.Default
            private int simulationDistance = -1;
        }
    }

    @Builder
    @Getter
    public static class Generation
    {
        @Builder.Default
        private World.Environment environment = World.Environment.NORMAL;
        @Builder.Default
        private boolean generateStructures = true;
        @Builder.Default
        private WorldType type = WorldType.NORMAL;
        @Builder.Default
        private String generator = null;
        @Builder.Default
        private String generatorSettings = null;
        @Builder.Default
        private String biomeProvider = null;
        @Builder.Default
        private long seed = Instant.now().getEpochSecond();
    }

    @Builder
    @Getter
    public static class Permissions
    {
        @Builder.Default
        private String access = "dimension.default.access";
        @Builder.Default
        private String blockBreak = "dimension.default.block_break";
        @Builder.Default
        private String blockPlace = "dimension.default.block_place";
        @Builder.Default
        private String die = "dimension.default.die";
        @Builder.Default
        private String entityAttack = "dimension.default.entity_attack";
        @Builder.Default
        private String entityInteract = "dimension.default.entity_interact";
        @Builder.Default
        private String entityTarget = "dimension.default.entity_target";
        @Builder.Default
        private String generalInteract = "dimension.default.general_interact";
        @Builder.Default
        private String dropItemsOnDeath = "dimension.default.drop_items_on_death";
    }
}
