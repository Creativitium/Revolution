package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "tptoggle",
        description = "Disable other players' ability to teleport to you.",
        usage = "/tptoggle [player]",
        permission = "basics.command.tptoggle",
        source = SourceType.BOTH)
public class Command_tptoggle extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;
        if (args.length >= 1 && !sender.hasPermission("basics.command.tptoggle.others"))
        {
            msg(sender, "basics.command.tptoggle.no_permission_others");
            return true;
        }

        final Optional<Player> target = args.length == 0 ? Optional.of(playerSender) : getPlayer(args[0]);

        target.ifPresentOrElse(player ->
        {
            final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(player.getUniqueId());
            data.setTpEnabled(!data.isTpEnabled());

            if (player != playerSender)
            {
                msg(sender, "basics.command.tptoggle." + (data.isTpEnabled() ? "enabled" : "disabled") + "_other",
                        Placeholder.unparsed("player", player.getName()));
            }

            msg(player, "basics.command.tptoggle." + (data.isTpEnabled() ? "enabled" : "disabled"));
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1 || !sender.hasPermission("basics.command.tptoggle.others")) return null;

        return match(getOnlinePlayers(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
