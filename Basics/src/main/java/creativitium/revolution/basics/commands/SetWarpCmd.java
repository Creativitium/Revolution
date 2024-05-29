package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.Warp;
import creativitium.revolution.basics.services.WarpsService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "setwarp",
        description = "Sets a location for anyone to teleport to later on.",
        usage = "/setwarp <warp>",
        permission = "basics.command.setwarp",
        source = SourceType.ONLY_IN_GAME)
public class SetWarpCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final WarpsService service = Basics.getInstance().getWarpsService();

        if (service.addWarp(args[0], Warp.builder().by(playerSender.getUniqueId())
                .position(playerSender.getLocation()).build(), playerSender))
        {
            msg(sender, "basics.command.setwarp.set");
            service.save();
        }
        else
        {
            msg(sender, "basics.command.setwarp.no_permission");
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(Basics.getInstance().getWarpsService().getWarpNamesBy(playerSender.getUniqueId()), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
