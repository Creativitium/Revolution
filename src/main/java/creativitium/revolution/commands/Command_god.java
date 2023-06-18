package creativitium.revolution.commands;

import creativitium.revolution.players.PlayerData;
import creativitium.revolution.templates.RCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "god",
        description = "Become invincible!",
        usage = "/god",
        permission = "revolution.command.god",
        aliases = {"iddqd"},
        source = SourceType.ONLY_IN_GAME)
public class Command_god extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        PlayerData data = plugin.pls.getPlayerData(playerSender.getUniqueId());
        data.setGodEnabled(!data.isGodEnabled());
        msg(sender, data.isGodEnabled() ? "revolution.command.god.enabled" : "revolution.command.god.disabled");

        return true;
    }
}
