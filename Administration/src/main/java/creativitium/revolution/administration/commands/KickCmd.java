package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "kick",
        description = "Kick a player from the server.",
        usage = "/kick <player> [reason]",
        permission = "administration.command.kick",
        source = SourceType.BOTH)
public class KickCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        String reason = args.length > 1 ? StringUtils.join(ArrayUtils.remove(args, 0), " ") : null;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            action(sender, "administration.action.kick", Placeholder.unparsed("player", player.getName()),
                    Placeholder.component("reason", reason != null ?
                            getMessage("administration.action.kick.reason", Placeholder.unparsed("reason", reason)) :
                            Component.empty()));

            player.kick(getMessage("administration.kick.message",
                    Placeholder.unparsed("by", sender.getName()),
                    Placeholder.component("reason", reason != null ?
                            getMessage("administration.kick.reason", Placeholder.unparsed("reason", reason)) :
                            Component.empty())));

        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
