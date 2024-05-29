package creativitium.revolution.basics.commands;

import com.google.common.collect.Lists;
import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.services.WarpsService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "warp",
        description = "Teleports you to a warp.",
        usage = "/warp <warp>",
        aliases = {"warps"},
        permission = "basics.command.warp",
        source = SourceType.ONLY_IN_GAME)
public class WarpCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final WarpsService service = Basics.getInstance().getWarpsService();

        if (args.length == 0)
        {
            if (service.getWarpCount() == 0)
            {
                msg(sender, "basics.command.warp.none_set");
                return true;
            }
            else
            {
                msg(sender, "basics.command.warp.list");
                Lists.partition(service.getWarpNames(), 25).forEach(partition ->
                        msg(sender, "basics.command.warp.list.continued", Placeholder.component("warps", Component.join(JoinConfiguration.commas(true),
                                partition.stream().map(Component::text).toList()))));
                return false;
            }
        }

        service.getWarp(args[0]).ifPresentOrElse(warp -> {
            if (warp.getPosition() == null)
            {
                msg(sender, "basics.command.warp.corrupted");
                service.removeWarp(args[0]);
                return;
            }

            msg(sender, "basics.command.warp.teleporting", Placeholder.unparsed("warp", args[0]));
            playerSender.teleportAsync(warp.getPosition(), PlayerTeleportEvent.TeleportCause.COMMAND);
        }, () -> msg(sender, "basics.command.warp.not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(Basics.getInstance().getWarpsService().getWarpNames(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
