package creativitium.revolution.dimension.data;

import creativitium.revolution.dimension.Dimension;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.SourceType;
import lombok.Data;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.time.Instant;

@Data
public class CustomWorld
{
    private String name;
    private Command command = null;
    private Generation generation;
    private Flags flags;
    private Permissions permissions = new Permissions();
    //--
    private World world = null;

    public final void load()
    {
        if (world == null || Bukkit.getWorld(name) == null || !Bukkit.getWorlds().contains(world))
        {
            world = generate();
        }

        if (command != null && command.enabled && Bukkit.getCommandMap().getCommand(name.replace(" ", "_").toLowerCase()) == null)
            Foundation.getInstance().getCommandLoader().loadCommandsManually("dimension", new WorldCommand(this));
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
        }

        return world;
    }

    @Getter
    public static class Command
    {
        private boolean enabled;
        private String description;
        private String usage;
        private String[] aliases = {""};
        private String permission;
    }

    @Getter
    public static class Flags
    {
        private boolean autosave = true;
        private Difficulty difficulty = Difficulty.EASY;
        private boolean hardcore;
        private boolean pvp;
        private SpawnFlags spawn;
        private Distances distances;

        public static class SpawnFlags
        {
            private boolean allowAnimals;
            private boolean allowMonsters;
            private boolean keepInMemory = true;
        }

        public static class Distances
        {
            private int sendViewDistance = -1;
            private int simulationDistance = -1;
        }
    }

    @Getter
    public static class Generation
    {
        private World.Environment environment = World.Environment.NORMAL;
        private boolean generateStructures = true;
        private WorldType type = WorldType.NORMAL;
        private String generator = null;
        private String generatorSettings = null;
        private String biomeProvider = null;
        private long seed = Instant.now().getEpochSecond();
    }

    @Getter
    public static class Permissions
    {
        private String access = "dimension.default.access";
        private String blockBreak = "dimension.default.block_break";
        private String blockPlace = "dimension.default.block_place";
        private String die = "dimension.default.die";
        private String entityAttack = "dimension.default.entity_attack";
        private String entityInteract = "dimension.default.entity_interact";
        private String entityTarget = "dimension.default.entity_target";
        private String generalInteract = "dimension.default.block_interact";
        private String dropItemsOnDeath = "dimension.default.drop_items_on_death";
    }
}
