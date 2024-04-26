package creativitium.revolution.foundation;

import creativitium.revolution.foundation.command.RCommand;
import org.bukkit.Bukkit;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
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
                Foundation.getSlf4jLogger().info("Found command " + commandClass.getName());
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

    public void loadCommandsManually(String prefix, RCommand... cmds)
    {
        Arrays.stream(cmds).forEach(command ->
        {
            Bukkit.getCommandMap().register(prefix.toLowerCase(), command.getInternalCommand());
            commands.add(command);
        });
    }
}
