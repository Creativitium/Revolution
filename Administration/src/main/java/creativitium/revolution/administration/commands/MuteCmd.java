package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.SPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "mute",
        description = "Shut someone up.",
        usage = "/mute <player>",
        aliases = {"stfu"},
        permission = "administration.command.mute",
        source = SourceType.BOTH)
public class MuteCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            if (player.hasPermission("administration.bypass.mutes"))
            {
                msg(sender, "administration.command.mute.immune");
                return;
            }

            final SPlayer p = (SPlayer) Shortcuts.getService(Key.key("administration", "sanctions")).getPlayerData(player.getUniqueId());

            if (p.isMuted())
            {
                msg(sender, "administration.command.mute.already_muted");
                return;
            }

            action(sender, "administration.action.mute", Placeholder.unparsed("player", player.getName()));
            p.setMuted(true);
            msg(player, "administration.command.mute.you_have_been_muted");
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
        return Administration.getInstance();
    }
}
