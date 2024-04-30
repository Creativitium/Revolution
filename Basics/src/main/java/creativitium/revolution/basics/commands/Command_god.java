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
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "god",
        description = "Become invincible!",
        usage = "/god",
        permission = "basics.command.god",
        aliases = {"iddqd"},
        source = SourceType.ONLY_IN_GAME)
public class Command_god extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(playerSender.getUniqueId());

        data.setGodEnabled(!data.isGodEnabled());
        msg(sender, "basics.command.god." + (data.isGodEnabled() ? "enabled" : "disabled"));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
