package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.data.APlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "adminchat",
        description = "Send a message to the admin chat.",
        usage = "/adminchat <message>",
        aliases = {"ac", "o"},
        permission = "administration.command.staffchat",
        source = SourceType.BOTH)
public class AdminChatCmd extends RCommand
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

            APlayer data = (APlayer) Shortcuts.getService(Key.key("administration", "admin_preferences")).getPlayerData(playerSender.getUniqueId());
            data.setAdminChatEnabled(!data.isAdminChatEnabled());

            msg(sender, "administration.command.adminchat." + (data.isAdminChatEnabled() ? "enabled" : "disabled"));
            return true;
        }

        ((Administration) getPlugin()).getAdminChatService().sendAdminChat(sender, StringUtils.join(args, " "));
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
