package creativitium.revolution;

import creativitium.revolution.templates.RCommand;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class CommandLoader
{
    private List<RCommand> commands = new ArrayList<>();

    public CommandLoader(String packaqe)
    {
        new Reflections(packaqe).getSubTypesOf(RCommand.class).forEach(commandClass ->
        {
            try
            {
                final RCommand command = commandClass.getDeclaredConstructor().newInstance();
                Bukkit.getCommandMap().register("revolution", command.getInternalCommand());
                commands.add(command);
            }
            catch (Throwable ex)
            {
                Revolution.getSlf4jLogger().error("Failed to load command " + commandClass.getName(), ex);
            }
        });

        Revolution.getSlf4jLogger().info("Successfully loaded and registered " + commands.size() + " commands");
    }
}
