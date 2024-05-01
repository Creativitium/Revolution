package creativitium.revolution.foundation;

import creativitium.revolution.foundation.command.RCommand;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

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
                final RCommand command = commandClass.getDeclaredConstructor().newInstance();
                Bukkit.getCommandMap().register(prefix.toLowerCase(), command.getInternalCommand());
                commands.add(command);
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().error("Failed to load command {}", commandClass.getName(), ex);
            }
        });

        Foundation.getSlf4jLogger().info("Successfully loaded and registered {} commands", commands.size());
    }

    public void loadCommandsManually(String prefix, Class<RCommand>... cmds)
    {
        Arrays.stream(cmds).forEach(command ->
        {
            try
            {
                final RCommand cmd = command.getDeclaredConstructor().newInstance();
                Bukkit.getCommandMap().register(prefix.toLowerCase(), cmd.getInternalCommand());
                commands.add(cmd);
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().error("Failed to load command {}", command.getName(), ex);
            }
        });
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
