package creativitium.revolution.dimension.services;

import com.google.gson.Gson;
import creativitium.revolution.dimension.Dimension;
import creativitium.revolution.dimension.data.CustomWorld;
import creativitium.revolution.foundation.templates.RService;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorldManager extends RService
{
    private static final Gson gson = new Gson();
    private WorldConfig config = null;

    public WorldManager()
    {
        super(Dimension.getInstance());
    }

    @Override
    public void onStart()
    {
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

                Files.copy(Objects.requireNonNull(getPlugin().getResource("worlds.json")), file.toPath());
            }
            catch (Exception ex)
            {
                getPlugin().getSLF4JLogger().error("Failed to copy world configuration file!", ex);
            }
        }

        try
        {
            WorldConfig config = gson.fromJson(new FileReader(file), WorldConfig.class);

            if (this.config == null)
                this.config = config;
            else
                this.config.combine(config);
        }
        catch (Exception ex)
        {
            getPlugin().getSLF4JLogger().error("Failed to load the world configuration. Worlds will not be generated!", ex);
        }

        if (this.config.settings.enabled)
            this.config.worlds.values().forEach(CustomWorld::load);
    }

    @Override
    public void onStop()
    {
        if (this.config != null && this.config.settings.enabled)
            this.config.worlds.values().forEach(world -> world.getWorld().save());
    }

    public static class WorldConfig
    {
        private Settings settings;
        private Map<String, CustomWorld> worlds = new HashMap<>();

        public void combine(WorldConfig config)
        {
            config.worlds.forEach((key, value) ->
            {
                if (!worlds.containsKey(key))
                    worlds.put(key, value);
            });
        }

        public static class Settings
        {
            private boolean enabled;
        }
    }
}
