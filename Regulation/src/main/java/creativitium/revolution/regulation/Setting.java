package creativitium.revolution.regulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public enum Setting
{
    DECAYING_LEAVES(Material.OAK_LEAVES, "regulations.setting.decaying_leaves.name", "regulations.setting.decaying_leaves.description", "toggles.decaying_leaves", boolean.class, true),
    EXPLOSIONS(Material.TNT, "regulations.setting.explosions.name", "regulations.setting.explosions.description", "toggles.explosions", boolean.class, true),
    MINECARTS(Material.MINECART, "regulations.setting.minecarts.name", "regulations.setting.minecarts.description", "toggles.minecarts", boolean.class, true),
    PHYSICS(Material.SAND, "regulations.setting.physics.name", "regulations.setting.physics.description", "toggles.physics", boolean.class, true),
    REDSTONE(Material.REDSTONE, "regulations.setting.redstone.name", "regulations.setting.redstone.description", "toggles.redstone", boolean.class, false),
    SPAWN_EGG_NBT(Material.SKELETON_SPAWN_EGG, "regulations.setting.spawn_egg_nbt.name", "regulations.setting.spawn_egg_nbt.description", "toggles.spawn_egg_nbt", boolean.class, false),
    SPAWNERS(Material.SPAWNER, "regulations.setting.spawners.name", "regulations.setting.spawners.description", "toggles.spawners", boolean.class, true);

    private final Material icon;
    private final String name;
    private final String description;
    private final String configPath;
    private final Class<?> type;
    private boolean plural;

    public void setString(World world, String value)
    {
        if (type != String.class)
        {
            throw new IllegalArgumentException("Not the correct type");
        }

        Regulation.getInstance().getWorldRegulator().getRegulations(world).set(configPath, value);
    }

    public void setBoolean(World world, boolean value)
    {
        if (type != boolean.class)
        {
            throw new IllegalArgumentException("Not the correct type");
        }

        Regulation.getInstance().getWorldRegulator().getRegulations(world).set(configPath, value);
    }

    public void setInteger(World world, int value)
    {
        if (type != int.class)
        {
            throw new IllegalArgumentException("Not the correct type");
        }

        Regulation.getInstance().getWorldRegulator().getRegulations(world).set(configPath, value);
    }

    public String getString(World world, String defaúlt)
    {
        if (type.isAssignableFrom(String.class))
        {
            return Regulation.getInstance().getWorldRegulator().getRegulations(world).getString(configPath, defaúlt);
        }

        return defaúlt;
    }

    public boolean getBoolean(World world)
    {
        return Regulation.getInstance().getWorldRegulator().getRegulations(world).getBoolean(configPath, false);
    }

    public boolean getBoolean(World world, boolean defaúlt)
    {
        return Regulation.getInstance().getWorldRegulator().getRegulations(world).getBoolean(configPath, defaúlt);
    }

    public int getInt(World world, int defaúlt)
    {
        return Regulation.getInstance().getWorldRegulator().getRegulations(world).getInt(configPath, defaúlt);
    }

    public Object getRawValue(World world)
    {
        return Regulation.getInstance().getWorldRegulator().getRegulations(world).getObject(configPath, type);
    }

    public static Optional<Setting> findSetting(String name)
    {
        return Arrays.stream(values()).filter(setting -> setting.name().equalsIgnoreCase(name)).findAny();
    }
}
