package creativitium.revolution.foundation;

import creativitium.revolution.foundation.command.RCommand;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class CommandLoader
{
    private final List<RCommand> commands = new ArrayList<>();

    public void loadCommands(String packaqe, String prefix)
    {
        new Reflections(packaqe).getSubTypesOf(RCommand.class).forEach(commandClass ->
        {
            try
            {
                final RCommand command = commandClass.getDeclaredConstructor().newInstance();
                Bukkit.getCommandMap().register(prefix.toLowerCase(), command.getInternalCommand());
                commands.add(command);
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().error("Failed to load command " + commandClass.getName(), ex);
            }
        });

        Foundation.getSlf4jLogger().info("Successfully loaded and registered " + commands.size() + " commands");
    }
}
