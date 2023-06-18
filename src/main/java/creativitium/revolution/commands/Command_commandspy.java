package creativitium.revolution.commands;

import creativitium.revolution.players.PlayerData;
import creativitium.revolution.templates.RCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "commandspy",
        description = "See other players' commands!",
        usage = "/commandspy",
        aliases = {"cmdspy"},
        permission = "revolution.command.commandspy",
        source = SourceType.ONLY_IN_GAME)
public class Command_commandspy extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final PlayerData playerData = plugin.pls.getPlayerData(playerSender.getUniqueId());

        playerData.setCommandSpyEnabled(!playerData.isCommandSpyEnabled());
        msg(sender, playerData.isCommandSpyEnabled() ? "revolution.command.commandspy.enabled"
                : "revolution.command.commandspy.disabled");
        return true;
    }
}
