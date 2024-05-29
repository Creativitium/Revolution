package creativitium.revolution.foundation.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * <h1>SourceType</h1>
 * <p>An enum for {@link org.bukkit.command.CommandSender} types that would be typically executing commands.</p>
 */
public enum SourceType
{
    ONLY_IN_GAME(Player.class),
    ONLY_CONSOLE(ConsoleCommandSender.class, RemoteConsoleCommandSender.class),
    BOTH(Player.class, ConsoleCommandSender.class, RemoteConsoleCommandSender.class);

    private final Class<? extends CommandSender>[] applicable;

    @SafeVarargs
    SourceType(Class<? extends CommandSender>... applicable)
    {
        this.applicable = applicable;
    }

    public boolean matchesSourceType(CommandSender sender)
    {
        return Arrays.stream(applicable).anyMatch(type -> type.isInstance(sender));
    }
}
