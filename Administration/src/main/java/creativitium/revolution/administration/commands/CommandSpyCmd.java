package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.APlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "commandspy",
        description = "Toggles CommandSpy - a tool that lets you see other players' commands.",
        usage = "/commandspy",
        aliases = {"cmdspy"},
        permission = "administration.command.commandspy",
        source = SourceType.ONLY_IN_GAME)
public class CommandSpyCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        APlayer data = (APlayer) Shortcuts.getService(Key.key("administration", "admin_preferences")).getPlayerData(playerSender.getUniqueId());
        data.setCommandSpyEnabled(!data.isCommandSpyEnabled());

        msg(sender, "administration.command.commandspy." + (data.isCommandSpyEnabled() ? "enabled" : "disabled"));
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
