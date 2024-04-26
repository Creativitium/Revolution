package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandParameters(name = "berserk",
        description = "Go berserk!",
        usage = "/berserk",
        permission = "basics.command.berserk",
        source = SourceType.ONLY_IN_GAME)
public class Command_berserk extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(playerSender.getUniqueId());

        data.setBerserkEnabled(!data.isBerserkEnabled());
        msg(sender, "basics.command.berserk." + (data.isBerserkEnabled() ? "enabled" : "disabled"));

        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
