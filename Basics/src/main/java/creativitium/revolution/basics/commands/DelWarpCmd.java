package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.services.WarpsService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "delwarp",
        description = "Deletes a warp.",
        usage = "/delwarp <warp>",
        permission = "basics.command.delwarp",
        source = SourceType.BOTH)
public class DelWarpCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final WarpsService service = Basics.getInstance().getWarpsService();

        service.getWarp(args[0]).ifPresentOrElse(warp ->
        {
            if (SourceType.ONLY_IN_GAME.matchesSourceType(sender) && warp.getBy() != playerSender.getUniqueId()
                    && !sender.hasPermission("basics.command.delwarp.others"))
            {
                msg(sender, "basics.command.delwarp.no_permission");
                return;
            }

            service.removeWarp(args[0]);
            service.save();
            msg(sender, "basics.command.delwarp.deleted", Placeholder.unparsed("warp", args[0]));
        }, () -> msg(sender, "basics.command.warp.not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final WarpsService service = Basics.getInstance().getWarpsService();

        return args.length == 1 ? match(SourceType.ONLY_CONSOLE.matchesSourceType(sender) || sender.hasPermission("basics.command.delwarp.others") ? service.getWarpNames() : service.getWarpNamesBy(playerSender.getUniqueId()), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
