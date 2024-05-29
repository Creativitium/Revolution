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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "message",
        description = "Send a message to a player!",
        usage = "/message <player> <message...>",
        aliases = {"msg"},
        permission = "basics.command.message",
        source = SourceType.ONLY_IN_GAME)
public class MessageCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 2) return false;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            final String message = String.join(" ", ArrayUtils.remove(args, 0));
            final BPlayer senderData = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(playerSender.getUniqueId());
            final BPlayer playerData = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId());

            senderData.setLastMessenger(player.getUniqueId());

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
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1) return null;

        return match(getOnlinePlayers(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
