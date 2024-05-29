package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "setspawn",
        description = "Set the current spawnpoint.",
        usage = "/setspawn",
        permission = "basics.command.setspawn",
        source = SourceType.ONLY_IN_GAME)
public class SetSpawnCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        Basics.getInstance().getBasicsService().setSpawnpoint(playerSender.getLocation());
        Basics.getInstance().getBasicsService().saveSpawn();
        msg(sender, "basics.command.setspawn.set");
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
