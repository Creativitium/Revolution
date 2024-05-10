package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "reply",
        description = "Reply to a message!",
        usage = "/reply <message...>",
        aliases = {"r"},
        permission = "basics.command.reply",
        source = SourceType.ONLY_IN_GAME)
public class Command_reply extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final BPlayer senderData = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(playerSender.getUniqueId());
        if (senderData.getLastMessenger() == null)
        {
            msg(sender, "basics.command.reply.nobody_to_reply_to");
            return true;
        }

        getPlayer(senderData.getLastMessenger().toString()).ifPresentOrElse(player ->
        {
            final String message = String.join(" ", args);
            final BPlayer playerData = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId());
            if (playerData.getLastMessenger() == null)
            {
                playerData.setLastMessenger(playerSender.getUniqueId());
            }

            msg(sender, "basics.command.message.sender",
                    Placeholder.parsed("name", player.getName()),
                    Placeholder.unparsed("message", message),
                    Placeholder.component("display", player.displayName()));

            msg(player, "basics.command.message.receiver",
                    Placeholder.parsed("name", sender.getName()),
                    Placeholder.unparsed("message", message),
                    Placeholder.component("display", playerSender.displayName()));
        }, () -> {
            msg(sender, "basics.command.reply.person_who_replied_last_went_offline");
            senderData.setLastMessenger(null);
        });
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
