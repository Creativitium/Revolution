package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.data.APlayer;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandParameters(name = "adminchat",
        description = "Send a message to the admin chat.",
        usage = "/adminchat <message>",
        aliases = {"ac", "o"},
        permission = "administration.components.staffchat",
        source = SourceType.BOTH)
public class Command_adminchat extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        // Toggle
        if (args.length == 0)
        {
            if (!SourceType.ONLY_IN_GAME.matchesSourceType(sender))
            {
                return false;
            }

            APlayer data = (APlayer) Shortcuts.getExternalPlayerService(Administration.getInstance()).getPlayerData(playerSender.getUniqueId());
            data.setAdminChatEnabled(!data.isAdminChatEnabled());

            msg(sender, "administration.command.adminchat." + (data.isAdminChatEnabled() ? "enabled" : "disabled"));
            return true;
        }

        ((Administration) getPlugin()).getAdminChatService().sendAdminChat(sender, StringUtils.join(args, " "));
        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
