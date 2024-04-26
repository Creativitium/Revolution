package creativitium.revolution.foundation.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public enum SourceType
{
    ONLY_IN_GAME,
    ONLY_CONSOLE,
    BOTH;

    public boolean matchesSourceType(CommandSender sender)
    {
        return (sender instanceof Player && this == ONLY_IN_GAME)
                || ((sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) && this == ONLY_CONSOLE)
                || this == BOTH;
    }
}
