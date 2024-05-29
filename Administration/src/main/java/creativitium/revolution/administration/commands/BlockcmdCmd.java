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

@CommandParameters(name = "blockcmd",
        description = "Prevent a player from executing any commands.",
        usage = "/blockcmd <player>",
        aliases = {"blockcmds", "blockcommand"},
        permission = "administration.command.blockcmd",
        source = SourceType.BOTH)
public class BlockcmdCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            if (player.hasPermission("administration.bypass.blocked_commands"))
            {
                msg(sender, "administration.command.blockcmd.immune");
                return;
            }

            final SPlayer p = (SPlayer) Shortcuts.getService(Key.key("administration", "sanctions")).getPlayerData(player.getUniqueId());

            if (p.isCommandsBlocked())
            {
                msg(sender, "administration.command.blockcmd.already_blocked");
                return;
            }

            action(sender, "administration.action.blockcmd", Placeholder.unparsed("player", player.getName()));
            p.setCommandsBlocked(true);
            msg(player, "administration.command.blockcmd.your_commands_have_been_blocked");
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
