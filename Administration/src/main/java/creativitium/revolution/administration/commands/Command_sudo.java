package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "sudo",
        description = "Run a command as somebody else.",
        usage = "/sudo <user> <command>",
        aliases = {"gcmd"},
        permission = "administration.command.sudo",
        source = SourceType.BOTH)
public class Command_sudo extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 2) return false;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            if (player.hasPermission("administration.command.sudo.immune") && !sender.hasPermission("administration.command.sudo.bypass"))
            {
                msg(sender, "administration.command.sudo.immune");
                return;
            }

            final String command = StringUtils.join(ArrayUtils.remove(args, 0), " ");

            msg(sender, "administration.command.sudo.performing_command", Placeholder.unparsed("command", command),
                    Placeholder.unparsed("username", player.getName()));

            player.performCommand(command);
        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(getOnlinePlayers(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
