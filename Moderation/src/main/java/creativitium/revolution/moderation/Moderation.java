package creativitium.revolution.moderation;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Moderation extends JavaPlugin
{
    @Getter
    private static Moderation instance;

    @Override
    public void onLoad()
    {
        instance = this;
    }
}
